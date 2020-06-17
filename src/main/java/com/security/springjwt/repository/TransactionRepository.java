package com.security.springjwt.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.security.springjwt.models.Transaction;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
	
	@Query("select new Transaction(t.id, t.date, t.amount, t.balance, t.description) from Transaction t where t.account.id=:accountId")
    List<Transaction> getAccountTransactionsHistory(@Param("accountId") Long accountId);
}
