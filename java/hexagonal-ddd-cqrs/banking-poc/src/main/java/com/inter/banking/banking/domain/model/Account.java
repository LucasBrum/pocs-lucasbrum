package com.inter.banking.banking.domain.model;

import com.inter.banking.banking.domain.exception.InsufficientBalanceException;
import com.inter.banking.banking.domain.exception.InvalidAmountException;
import com.inter.banking.banking.domain.model.enums.AccountStatus;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@EqualsAndHashCode
public class Account {
  private final AccountId id;
  private final String accountNumber;
  private final CustomerId customerId;
  private Money balance;
  private AccountStatus status;
  private final LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  // Construtor principal
  public Account(AccountId id, String accountNumber, CustomerId customerId, Money initialBalance) {
    this.id = Objects.requireNonNull(id, "Account ID cannot be null");
    this.accountNumber = Objects.requireNonNull(accountNumber, "Account number cannot be null");
    this.customerId = Objects.requireNonNull(customerId, "Customer ID cannot be null");
    this.balance = Objects.requireNonNull(initialBalance, "Initial balance cannot be null");
    this.status = AccountStatus.ACTIVE;
    this.createdAt = LocalDateTime.now();
    this.updatedAt = LocalDateTime.now();

    validateInitialBalance(initialBalance);
  }

  // Construtor para reconstruir do banco (usado pelo Repository)
  public Account(
      AccountId id,
      String accountNumber,
      CustomerId customerId,
      Money balance,
      AccountStatus status,
      LocalDateTime createdAt,
      LocalDateTime updatedAt) {

    this.id = id;
    this.accountNumber = accountNumber;
    this.customerId = customerId;
    this.balance = balance;
    this.status = status;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  /**
   * Debita um valor da conta Regras: - Conta deve estar ativa - Valor deve ser positivo - Saldo
   * deve ser suficiente
   */
  public void debit(Money amount) {
    validateAmount(amount);
    validateAccountIsActive();

    if (balance.isLessThan(amount)) {
      throw new InsufficientBalanceException("Insufficient balance for debit operation");
    }

    this.balance = this.balance.subtract(amount);
    this.updatedAt = LocalDateTime.now();
  }

  /** Credita um valor na conta Regras: - Conta deve estar ativa - Valor deve ser positivo */
  public void credit(Money amount) {
    validateAmount(amount);
    validateAccountIsActive();

    this.balance = this.balance.add(amount);
    this.updatedAt = LocalDateTime.now();
  }

  /** Bloqueia a conta */
  public void block() {
    this.status = AccountStatus.BLOCKED;
    this.updatedAt = LocalDateTime.now();
  }

  /** Desbloqueia a conta */
  public void unblock() {
    this.status = AccountStatus.ACTIVE;
    this.updatedAt = LocalDateTime.now();
  }

  // Métodos de consulta (queries)

  /** Verifica se a conta pode debitar um valor Não altera estado - pure function */
  public boolean canDebit(Money amount) {
    return status == AccountStatus.ACTIVE && balance.isGreaterThanOrEqual(amount);
  }

  /** Verifica se a conta está ativa */
  public boolean isActive() {
    return status == AccountStatus.ACTIVE;
  }

  // Validações privadas (invariantes do domínio)

  private void validateAmount(Money amount) {
    if (amount.isZeroOrNegative()) {
      throw new InvalidAmountException("Amount must be positive");
    }
  }

  private void validateAccountIsActive() {
    if (status != AccountStatus.ACTIVE) {
      throw new IllegalStateException("Account is not active");
    }
  }

  private void validateInitialBalance(Money initialBalance) {
    if (initialBalance.isNegative()) {
      throw new InvalidAmountException("Initial balance cannot be negative");
    }
  }
}
