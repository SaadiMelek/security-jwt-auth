package com.security.springjwt.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.security.springjwt.models.Account;
import com.security.springjwt.payload.response.AccountResponse;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
	
	//List<AccountResponse> findAllByCustomerUsername(String username);
	
	@Query("select new com.security.springjwt.models.Account(a.id, a.type, a.description, a.balance, a.creditLine) from Account a where a.customer.username=:uname")
    List<Account> findAllByCustomerUsername(@Param("uname") String username);
}
