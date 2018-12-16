package io.everitoken.sdk.java;

import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.Sha256Hash;
import org.spongycastle.crypto.digests.SHA256Digest;
import org.spongycastle.crypto.params.ECPrivateKeyParameters;
import org.spongycastle.crypto.params.ECPublicKeyParameters;
import org.spongycastle.crypto.signers.ECDSASigner;
import org.spongycastle.crypto.signers.HMacDSAKCalculator;

import java.math.BigInteger;

public class Signature {
    private ECKey.ECDSASignature signature;
    private Integer recId;

    public Signature(BigInteger r, BigInteger s) {
        signature = new ECKey.ECDSASignature(r, s);
    }

    private ECKey.ECDSASignature get() {
        return signature;
    }

    public BigInteger getR() {
        return signature.r;
    }

    public BigInteger getS() {
        return signature.s;
    }

    private void setRecId(int id) {
        recId = id;
    }

    public Integer getRecId() {
        return recId;
    }

    public boolean isCanonical() {
        return signature.isCanonical();
    }

    private Signature toCanonicalised() {
        signature = signature.toCanonicalised();
        return this;
    }

    /**
     * Sign 32 bits hash with private key and store the recId into signature
     *
     * @param data Data hash with 32 bits
     * @param key  PrivateKey
     * @return Signature
     */
    public static Signature sign(byte[] data, PrivateKey key) throws EvtSdkException {
        byte[] hash = Sha256Hash.hashTwice(data);

        // init deterministic k calculator
        ECDSASigner signer = new ECDSASigner(new HMacDSAKCalculator(new SHA256Digest()));
        ECPrivateKeyParameters privKey = new ECPrivateKeyParameters(key.getD(), ECKey.CURVE);

        signer.init(true, privKey);
        BigInteger[] components = signer.generateSignature(hash);
        Signature sig = new Signature(components[0], components[1]).toCanonicalised();

        // find the recId and store in signature for public key recover later
        PublicKey publicKey = key.toPublicKey();
        int recId = getRecId(sig, data, publicKey);

        if (recId == -1) {
            throw new EvtSdkException(null, ErrorCode.REC_ID_NOT_FOUND);
        }

        sig.setRecId(recId);

        return sig;
    }

    /**
     * Calculate the recover id from signature with original data bytes and reference public key
     */
    private static int getRecId(Signature signature, byte[] data, PublicKey publicKey) {
        Sha256Hash dataHash = Sha256Hash.twiceOf(data);

        String refPubKey = publicKey.getEncoded(true);

        int recId = -1;
        for (int i = 0; i < 4; i++) {
            ECKey k = ECKey.recoverFromSignature(i, signature.get(), dataHash, true);
            try {
                if (k != null && Utils.HEX.encode(k.getPubKey()).equals(refPubKey)) {
                    return i;
                }
            } catch (Exception ex) {
                // no need to handle anything here
            }
        }

        return recId;
    }

    /**
     * Verify hash message with signature and public key
     *
     * @param data      Data hash with 32 bits
     * @param signature Signature object
     * @param publicKey PublicKey object
     * @return boolean
     */
    public static boolean verify(byte[] data, Signature signature, PublicKey publicKey) {
        byte[] hash = Sha256Hash.hashTwice(data);

        ECDSASigner signer = new ECDSASigner(new HMacDSAKCalculator(new SHA256Digest()));

        ECPublicKeyParameters publicKeyParams = new ECPublicKeyParameters(
                publicKey.getPoint().get(), // ECPoint
                ECKey.CURVE
        );

        signer.init(false, publicKeyParams);

        return signer.verifySignature(hash, signature.getR(), signature.getS());
    }

    /**
     * Recover public key from signature and original data byte[].
     * Note: one always need to compare the public key recovered from signature match with whe reference public key
     *
     * @param data      original data signed by the private key
     * @param signature signature from sign method
     * @return
     * @throws EvtSdkException
     */
    public static PublicKey recoverPublicKey(byte[] data, Signature signature) throws EvtSdkException {
        Sha256Hash dataHash = Sha256Hash.twiceOf(data);

        try {
            ECKey k = ECKey.recoverFromSignature(signature.getRecId(), signature.get(), dataHash, true);
            if (k != null) {
                return new PublicKey(k.getPubKey());
            }

            throw new EvtSdkException(null, ErrorCode.RECOVER_PUB_FROM_SIG_FAILURE);
        } catch (Exception ex) {
            throw new EvtSdkException(null, ErrorCode.RECOVER_PUB_FROM_SIG_FAILURE);
        }
    }
}