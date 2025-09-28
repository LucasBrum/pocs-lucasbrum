package com.inter.banking.banking.application.query.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AccountDto(
        String id,
        String accountNumber,
        String customerId,
        BigDecimal balance,
        String currency,
        String status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
