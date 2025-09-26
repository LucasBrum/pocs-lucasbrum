package com.inter.banking.banking.domain.model;

import java.util.Objects;
import java.util.UUID;

/**
 * Value Object para ID do cliente
 */
public class CustomerId {
    private final UUID value;

    public CustomerId(UUID value) {
        this.value = Objects.requireNonNull(value, "Customer ID cannot be null");
    }

    public static CustomerId generate() {
        return new CustomerId(UUID.randomUUID());
    }

    public static CustomerId from(String value) {
        return new CustomerId(UUID.fromString(value));
    }

    public UUID getValue() { return value; }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        CustomerId customerId = (CustomerId) obj;
        return Objects.equals(value, customerId.value);
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
