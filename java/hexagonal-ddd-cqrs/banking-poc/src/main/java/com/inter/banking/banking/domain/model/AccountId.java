package com.inter.banking.banking.domain.model;

import java.util.Objects;
import java.util.UUID;

/**
 * Value Object para ID da conta
 * Evita primitive obsession e adiciona type safety
 */
public class AccountId {
    private final UUID value;

    public AccountId(UUID value) {
        this.value = Objects.requireNonNull(value, "Account ID cannot be null");
    }

    public static AccountId generate() {
        return new AccountId(UUID.randomUUID());
    }

    public static AccountId from(String value) {
        return new AccountId(UUID.fromString(value));
    }

    public UUID getValue() {return value;}

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        AccountId accountId = (AccountId) obj;
        return Objects.equals(value, accountId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
