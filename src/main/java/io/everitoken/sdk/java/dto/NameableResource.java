package io.everitoken.sdk.java.dto;

import com.alibaba.fastjson.JSON;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class NameableResource implements Namable {
    protected String name;

    NameableResource(String name) {
        this.name = name;
    }

    @NotNull
    @Contract("_ -> new")
    public static NameableResource create(String name) {
        return new NameableResource(name);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
