package io.everitoken.sdk.java.example;

import java.util.Arrays;

import org.json.JSONObject;

import io.everitoken.sdk.java.PublicKey;
import io.everitoken.sdk.java.abi.NewDomainAction;
import io.everitoken.sdk.java.dto.TransactionData;
import io.everitoken.sdk.java.exceptions.ApiResponseException;
import io.everitoken.sdk.java.param.NetParams;
import io.everitoken.sdk.java.param.TestNetNetParams;
import io.everitoken.sdk.java.provider.KeyProvider;
import io.everitoken.sdk.java.service.TransactionConfiguration;
import io.everitoken.sdk.java.service.TransactionService;

public class NewDomainExample {
    public static void main(String[] args) {
        final NetParams netParam = new TestNetNetParams();
        final String data = "{\"name\":\"test1124\","
                + "\"creator\":\"EVT6Qz3wuRjyN6gaU3P3XRxpnEZnM4oPxortemaWDwFRvsv2FxgND\",\"issue\":{\"name\":\"issue\","
                + "\"threshold\":1,\"authorizers\":[{\"ref\":\"[A] "
                + "EVT6Qz3wuRjyN6gaU3P3XRxpnEZnM4oPxortemaWDwFRvsv2FxgND\","
                + "\"weight\":1}]},\"transfer\":{\"name\":\"transfer\",\"threshold\":1,\"authorizers\":[{\"ref\":\"[G] "
                + ".OWNER\",\"weight\":1}]},\"manage\":{\"name\":\"manage\",\"threshold\":1,"
                + "\"authorizers\":[{\"ref\":\"[A]"
                + " EVT6Qz3wuRjyN6gaU3P3XRxpnEZnM4oPxortemaWDwFRvsv2FxgND\",\"weight\":1}]}}";

        final JSONObject json = new JSONObject(data);
        final NewDomainAction newDomainAction = NewDomainAction.ofRaw(json.getString("name"), json.getString("creator"),
                json.getJSONObject("issue"), json.getJSONObject("transfer"), json.getJSONObject("manage"));

        try {
            TransactionService transactionService = TransactionService.of(netParam);
            TransactionConfiguration trxConfig = new TransactionConfiguration(1000000,
                    PublicKey.of("EVT6Qz3wuRjyN6gaU3P3XRxpnEZnM4oPxortemaWDwFRvsv2FxgND"),
                    KeyProvider.of("5J1by7KRQujRdXrurEsvEr2zQGcdPaMJRjewER6XsAR2eCcpt3D"));
            TransactionData txData = transactionService.push(trxConfig, Arrays.asList(newDomainAction));
            System.out.println(txData.getTrxId());
        } catch (final ApiResponseException ex) {
            System.out.println(ex.getRaw());
        }
    }
}
