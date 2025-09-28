package com.inter.banking.banking.domain.port;

import com.inter.banking.banking.domain.model.Account;
import com.inter.banking.banking.domain.model.AccountId;

/**
 * Port para auditoria (Secondary/Driven Port)
 * Registra eventos importantes do sistema
 */
public interface AuditService {
    void logAccountCreation(Account account);
    void logTransaction(AccountId accountId, String operation, String amount, String result);
    void logAccountStatusChange(AccountId accountId, String oldStatus, String newStatus);
}
