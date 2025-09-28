package com.inter.banking.banking.domain.port;

/**
 * Port para geração de números de conta (Secondary/Driven Port)
 * Permite diferentes estratégias de geração
 */
public interface AccountNumberGenerator {
    String generateAccountNumber();
}
