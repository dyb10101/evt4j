package io.everitoken.sdk.java.dto;

import java.util.Objects;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

public class Charge {
    private final int charge;

    private Charge(int charge) {
        this.charge = charge;
    }

    @NotNull
    @Contract("_ -> new")
    public static Charge of(int charge) {
        return new Charge(charge);
    }

    @Contract("_ -> new")
    @NotNull
    public static Charge ofRaw(@NotNull JSONObject raw) {
        Objects.requireNonNull(raw);
        int charge = raw.getInt("charge");
        return new Charge(charge);
    }

    public int getCharge() {
        return charge;
    }
}
