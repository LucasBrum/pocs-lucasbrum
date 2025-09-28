package com.inter.banking.banking.application.query.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AccountBalanceDto(
        String accountId,
        String accountNumber,
        BigDecimal balance,
        String currency,
        LocalDateTime lastUpdated
) {}
