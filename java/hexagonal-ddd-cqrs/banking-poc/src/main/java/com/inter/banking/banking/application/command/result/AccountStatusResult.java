package com.inter.banking.banking.application.command.result;

public record AccountStatusResult(
        String accountId,
        String status
) {}
