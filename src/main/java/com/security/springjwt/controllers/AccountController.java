package com.security.springjwt.controllers;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.security.springjwt.models.Account;
import com.security.springjwt.models.Transaction;
import com.security.springjwt.repository.AccountRepository;
import com.security.springjwt.repository.TransactionRepository;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/account")
public class AccountController {
	
	@Autowired
	AccountRepository accountRepository;
	
	@Autowired
	TransactionRepository transactionRepository;
	
	@GetMapping("/all")
	public List<Account> currentUserAccounts(Principal principal) {
		final String loggedInUserName = principal.getName();
		System.err.println("loggedInUserName = " + loggedInUserName);
		return accountRepository.findAllByCustomerUsername(loggedInUserName);
		//return currentUserAccounts;
	}
	
	@GetMapping(path = "/history/{id}")
	public List<Transaction> getAccountTransactionsHistory(@PathVariable(name = "id") Long id) throws Exception {
		List<Transaction> transactions = transactionRepository.getAccountTransactionsHistory(id);
		return transactions;
	}
	
	/*@GetMapping("/user")
	@PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
	public String userAccess() {
		return "User Content.";
	}

	@GetMapping("/mod")
	@PreAuthorize("hasRole('MODERATOR')")
	public String moderatorAccess() {
		return "Moderator Board.";
	}

	@GetMapping("/admin")
	@PreAuthorize("hasRole('ADMIN')")
	public String adminAccess() {
		return "Admin Board.";
	}*/
}
