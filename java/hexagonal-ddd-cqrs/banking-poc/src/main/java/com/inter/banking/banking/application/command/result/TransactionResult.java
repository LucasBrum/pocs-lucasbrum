package com.inter.banking.banking.application.command.result;

import java.math.BigDecimal;

public record TransactionResult(
        String accountId,
        String transactionId,
        BigDecimal newBalance,
        String status
) {}
