package com.inter.banking.banking.application.query.dto;

import java.math.BigDecimal;
import java.util.List;

public record CustomerAccountsDto(
        String customerId,
        List<AccountDto> accounts,
        BigDecimal totalBalance
) {}
