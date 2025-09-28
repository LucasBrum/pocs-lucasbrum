package com.inter.banking.banking.domain.port;

import com.inter.banking.banking.domain.model.Account;
import com.inter.banking.banking.domain.model.AccountId;
import com.inter.banking.banking.domain.model.CustomerId;

import java.util.List;
import java.util.Optional;

/**
 * Port para persistência de contas (Secondary/Driven Port) Define o contrato que o domínio espera
 * para persistência Implementação fica na camada de infraestrutura
 */
public interface AccountRepository {
  Account save(Account acount);

  Optional<Account> findById(AccountId id);

  Optional<Account> findByAccountNumber(String accountNumber);

  List<Account> findByCustomerId(CustomerId customerId);

  void delete(AccountId id);

  boolean existsByAccountNumber(String accountNumber);
}
