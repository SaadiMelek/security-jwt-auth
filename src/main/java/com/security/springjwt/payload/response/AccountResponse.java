package com.security.springjwt.payload.response;

public class AccountResponse {

	private Long id;

	private String type;

	private String description;

	private Double balance;

	private Double creditLine;


	public AccountResponse() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	

	public AccountResponse(Long id, String type, String description, Double balance, Double creditLine) {
		super();
		this.id = id;
		this.type = type;
		this.description = description;
		this.balance = balance;
		this.creditLine = creditLine;
	}



	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Double getBalance() {
		return balance;
	}

	public void setBalance(Double balance) {
		this.balance = balance;
	}

	public Double getCreditLine() {
		return creditLine;
	}

	public void setCreditLine(Double creditLine) {
		this.creditLine = creditLine;
	}

}
