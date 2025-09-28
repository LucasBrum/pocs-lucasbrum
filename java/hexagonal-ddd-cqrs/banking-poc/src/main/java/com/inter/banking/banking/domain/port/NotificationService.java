package com.inter.banking.banking.domain.port;

import com.inter.banking.banking.domain.model.Account;
import com.inter.banking.banking.domain.model.AccountId;

/**
 * Port para serviços de notificação (Secondary/Driven Port)
 * O domínio define o que precisa, infraestrutura implementa como
 */
public interface NotificationService {
    void notifyAccountCreated(Account account);
    void notifyTransactionCompleted(AccountId accountId, String transactionType, String amount);
    void notifyAccountBlocked(Account account);
}
