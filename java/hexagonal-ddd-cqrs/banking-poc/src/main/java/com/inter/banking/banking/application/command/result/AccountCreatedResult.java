package com.inter.banking.banking.application.command.result;

public record AccountCreatedResult(
        String accountId,
        String accountNumber,
        String status
) {}
