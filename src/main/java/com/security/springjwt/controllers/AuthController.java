package com.security.springjwt.controllers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.number.money.CurrencyUnitFormatter;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.security.springjwt.models.Account;
import com.security.springjwt.models.ERole;
import com.security.springjwt.models.Role;
import com.security.springjwt.models.Transaction;
import com.security.springjwt.models.User;
import com.security.springjwt.payload.request.LoginRequest;
import com.security.springjwt.payload.request.SignupRequest;
import com.security.springjwt.payload.response.JwtResponse;
import com.security.springjwt.payload.response.MessageResponse;
import com.security.springjwt.repository.RoleRepository;
import com.security.springjwt.repository.UserRepository;
import com.security.springjwt.security.jwt.JwtUtils;
import com.security.springjwt.security.services.UserDetailsImpl;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	UserRepository userRepository;

	@Autowired
	RoleRepository roleRepository;

	@Autowired
	PasswordEncoder encoder;

	@Autowired
	JwtUtils jwtUtils;

	@PostMapping("/signin")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = jwtUtils.generateJwtToken(authentication);
		
		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();		
		List<String> roles = userDetails.getAuthorities().stream()
				.map(item -> item.getAuthority())
				.collect(Collectors.toList());

		return ResponseEntity.ok(new JwtResponse(jwt, 
												 userDetails.getId(), 
												 userDetails.getUsername(), 
												 userDetails.getEmail(), 
												 roles));
	}

	@PostMapping("/signup")
	public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) throws ParseException {
		if (userRepository.existsByUsername(signUpRequest.getUsername())) {
			return ResponseEntity
					.badRequest()
					.body(new MessageResponse("Error: Username is already taken!"));
		}

		if (userRepository.existsByEmail(signUpRequest.getEmail())) {
			return ResponseEntity
					.badRequest()
					.body(new MessageResponse("Error: Email is already in use!"));
		}

		// Create new user's account
		User user = new User(signUpRequest.getUsername(), 
							 signUpRequest.getEmail(),
							 encoder.encode(signUpRequest.getPassword()));

		Set<String> strRoles = signUpRequest.getRole();
		Set<Role> roles = new HashSet<>();

		if (strRoles == null) {
			Role userRole = roleRepository.findByName(ERole.ROLE_USER)
					.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
			roles.add(userRole);
		} else {
			strRoles.forEach(role -> {
				switch (role) {
				case "admin":
					Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
							.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
					roles.add(adminRole);

					break;
				case "mod":
					Role modRole = roleRepository.findByName(ERole.ROLE_MODERATOR)
							.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
					roles.add(modRole);

					break;
				default:
					Role userRole = roleRepository.findByName(ERole.ROLE_USER)
							.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
					roles.add(userRole);
				}
			});
		}
		
		Account current = new Account();
		current.setType("Compte courant");
		current.setDescription("Votre compte courant la ou vous pouvez effectuer des virement/retrait");
		current.setBalance(4000d);
		current.setCustomer(user);
		
		List<Transaction> currentTransactions = new ArrayList<>();
		
		String sourceDate = "2020-05-01 12:05";
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm");
		Date myDate = format.parse(sourceDate);
		for (int i = 0; i < 4; i++) {
			Transaction currentTransaction = new Transaction();
			currentTransaction.setAccount(current);
			currentTransaction.setAmount(1000d);
			currentTransaction.setBalance(1000d);
			myDate = this.addDays(myDate, 1);
			currentTransaction.setDate(myDate);
			currentTransaction.setDescription("Virement compte courant [Opération '" + (i+1) + "']");
			currentTransactions.add(currentTransaction);
		}
		
		current.setTransactions(currentTransactions);
		
		//compte epargne
		Account saving = new Account();
		saving.setType("Compte épargne");
		saving.setDescription("Votre compte épargne vous permet de ...");
		saving.setBalance(6000d);
		saving.setCustomer(user);
		
		List<Transaction> savingTransactions = new ArrayList<>();

		String savingSourceDate = "2020-06-01 12:05";
		SimpleDateFormat savingFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm");
		Date savingDate = savingFormat.parse(savingSourceDate);
		for (int i = 0; i < 3; i++) {
			Transaction savingTransaction = new Transaction();
			savingTransaction.setAccount(saving);
			savingTransaction.setAmount(2000d);
			savingTransaction.setBalance(2000d);
			savingDate = this.addDays(savingDate, 1);
			savingTransaction.setDate(savingDate);
			savingTransaction.setDescription("Virement vers compte épargne [Opération '" + (i+1) + "']");
			savingTransactions.add(savingTransaction);
		}
		
		saving.setTransactions(savingTransactions);
		
		
		List<Account> accounts = new ArrayList<>();
		accounts.add(current);
		accounts.add(saving);

		user.setRoles(roles);
		user.setAccounts(accounts);
		userRepository.save(user);

		return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
	}
	
	public Date addDays(Date date, int days)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days); //minus number would decrement the days
        return cal.getTime();
    }
}
