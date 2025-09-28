package com.inter.banking.banking.application.service;

import com.inter.banking.banking.application.command.*;
import com.inter.banking.banking.application.command.result.AccountCreatedResult;
import com.inter.banking.banking.application.command.result.AccountStatusResult;
import com.inter.banking.banking.application.command.result.TransactionResult;
import com.inter.banking.banking.domain.exception.AccountNotFoundException;
import com.inter.banking.banking.domain.model.Account;
import com.inter.banking.banking.domain.model.AccountId;
import com.inter.banking.banking.domain.model.CustomerId;
import com.inter.banking.banking.domain.model.Money;
import com.inter.banking.banking.domain.port.AccountNumberGenerator;
import com.inter.banking.banking.domain.port.AccountRepository;
import com.inter.banking.banking.domain.port.AuditService;
import com.inter.banking.banking.domain.port.NotificationService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@Transactional
public class AccountCommandService {
  private final AccountRepository accountRepository;
  private final AccountNumberGenerator accountNumberGenerator;
  private final NotificationService notificationService;
  private final AuditService auditService;

  public AccountCommandService(
      AccountRepository accountRepository,
      AccountNumberGenerator accountNumberGenerator,
      NotificationService notificationService,
      AuditService auditService) {
    this.accountRepository = accountRepository;
    this.accountNumberGenerator = accountNumberGenerator;
    this.notificationService = notificationService;
    this.auditService = auditService;
  }

  /**
   * Use Case: Criar nova conta
   */
  public AccountCreatedResult createdAccount(CreateAccountCommand command) {
    log.info("Criando nova conta para cliente: " + command.customerId());

    // 1. Gerar dados necessários
    AccountId accountId = AccountId.generate();
    String accountNumber = generateUniqueAccountNumber();
    CustomerId customerId = CustomerId.from(command.customerId());
    Money initialBalance = Money.brl(command.initialBalance());

    // 2. Criar conta usando o domínio (aqui as regras são aplicadas)
    Account account = new Account(accountId, accountNumber, customerId, initialBalance);

    // 3. Persistir
    Account savedAccount = accountRepository.save(account);

    // 4. Efeitos colaterais (notificações, auditoria)
    auditService.logAccountCreation(savedAccount);
    notificationService.notifyAccountCreated(savedAccount);

    log.info("Conta criada com sucesso: " + savedAccount.getAccountNumber());

    return new AccountCreatedResult(
            savedAccount.getId().toString(),
            savedAccount.getAccountNumber(),
            savedAccount.getStatus().toString()
    );
  }

  /**
   * Use Case: Debitar conta
   */

  public TransactionResult debitAccount(DebitAccountCommand command) {
    log.info("Debitando R$ " + command.amount() + " da conta: " + command.accountId());

    // 1. Buscar conta
    Account account = findAccountById(command.accountId());

    // 2. Executar operação de domínio (regras são aplicadas na entity)
    Money amount = Money.brl(command.amount());
    account.debit(amount);

    // 3. Persistir
    Account savedAccount = accountRepository.save(account);

    // 4. Efeitos colaterais
    String transactionId = generateTransactionId();
    auditService.logTransaction(account.getId(), "DEBIT", amount.toString(), "SUCCESS");
    notificationService.notifyTransactionCompleted(account.getId(), "DEBIT", amount.toString());

    log.info("Débito realizado. Novo saldo: R$ " + savedAccount.getBalance().getAmount());

    return new TransactionResult(
            savedAccount.getId().toString(),
            transactionId,
            savedAccount.getBalance().getAmount(),
            "COMPLETED"
    );
  }

  /**
   * Use Case: Creditar conta
   */
  public TransactionResult creditAccount(CreditAccountCommand command) {
    log.info("💰 Creditando R$ " + command.amount() + " na conta: " + command.accountId());

    // 1. Buscar conta
    Account account = findAccountById(command.accountId());

    // 2. Executar operação de domínio
    Money amount = Money.brl(command.amount());
    account.credit(amount);

    // 3. Persistir
    Account savedAccount = accountRepository.save(account);

    // 4. Efeitos colaterais
    String transactionId = generateTransactionId();
    auditService.logTransaction(
            account.getId(),
            "CREDIT",
            amount.toString(),
            "SUCCESS"
    );
    notificationService.notifyTransactionCompleted(
            account.getId(),
            "CREDIT",
            amount.toString()
    );

    log.info("Crédito realizado. Novo saldo: R$ " + savedAccount.getBalance().getAmount());

    return new TransactionResult(
            savedAccount.getId().toString(),
            transactionId,
            savedAccount.getBalance().getAmount(),
            "COMPLETED"
    );
  }

  public AccountStatusResult blockAccount(BlockAccountCommand command) {
    log.info("Bloqueando conta: " + command.accountId() + " - Motivo: " + command.reason());

    // 1. Buscar conta
    Account account = findAccountById(command.accountId());
    String oldStatus = account.getStatus().toString();

    // 2. Executar operação de domínio
    account.block();

    // 3. Persistir
    Account savedAccount = accountRepository.save(account);

    // 4. Efeitos colaterais
    auditService.logAccountStatusChange(
            account.getId(),
            oldStatus,
            savedAccount.getStatus().toString()
    );
    notificationService.notifyAccountBlocked(savedAccount);

    log.info("Conta bloqueada com sucesso: " + savedAccount.getBalance().getAmount());

    return new AccountStatusResult(
            savedAccount.getId().toString(),
            savedAccount.getStatus().toString()
    );

  }

  /**
   * Use Case: Desbloquear conta
   */
  public AccountStatusResult unblockAccount(UnblockAccountCommand command) {
    log.info("Desbloqueando conta: " + command.accountId());

    // 1. Buscar conta
    Account account = findAccountById(command.accountId());
    String oldStatus = account.getStatus().toString();

    // 2. Executar operação de domínio
    account.unblock();

    // 3. Persistir
    Account savedAccount = accountRepository.save(account);

    // 4. Efeitos colaterais
    auditService.logAccountStatusChange(
            account.getId(),
            oldStatus,
            savedAccount.getStatus().toString()
    );

    log.info("✅ Conta desbloqueada com sucesso");

    return new AccountStatusResult(
            savedAccount.getId().toString(),
            savedAccount.getStatus().toString()
    );
  }

  private Account findAccountById(String accountId) {
    return accountRepository.findById(AccountId.from(accountId))
            .orElseThrow(() -> new AccountNotFoundException("Account not found: " + accountId));
  }

  private String generateUniqueAccountNumber() {
    String accountNumber;

    do {
      accountNumber = accountNumberGenerator.generateAccountNumber();
    } while (accountRepository.existsByAccountNumber(accountNumber));

    return accountNumber;
  }

  private String generateTransactionId() {
    return UUID.randomUUID().toString();
  }
}
