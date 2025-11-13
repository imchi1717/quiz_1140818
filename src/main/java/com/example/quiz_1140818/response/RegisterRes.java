package com.example.quiz_1140818.response;

public class RegisterRes extends BasicRes {

	private String account;

	private String email;

	private String name;

	private String phone;

	private int age;

	private boolean admin;

	public RegisterRes() {
		super();
	}

	public RegisterRes(int code, String message) {
		super(code, message);
	}

	public RegisterRes(String account, String email, String name, String phone, int age, boolean admin) {
		super();
		this.account = account;
		this.email = email;
		this.name = name;
		this.phone = phone;
		this.age = age;
		this.admin = admin;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public boolean isAdmin() {
		return admin;
	}

	public void setAdmin(boolean admin) {
		this.admin = admin;
	}

	
}
