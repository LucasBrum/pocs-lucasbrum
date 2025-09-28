package com.inter.banking.banking.application.command;

import java.math.BigDecimal;

public record DebitAccountCommand(
        String accountId,
        BigDecimal amount,
        String description
) {}
