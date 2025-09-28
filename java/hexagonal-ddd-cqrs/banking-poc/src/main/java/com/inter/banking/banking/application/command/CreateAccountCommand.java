package com.inter.banking.banking.application.command;

import java.math.BigDecimal;

/**
 * Command para criação de conta
 * Records são perfeitos para DTOs imutáveis
 */
public record CreateAccountCommand(
        String customerId,
        BigDecimal initialBalance
){}
