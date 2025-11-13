package com.example.quiz_1140818.response;

public class LoginRes extends BasicRes{
	
	// 判斷登入帳號是否為管理者
	private String account;
	
	private boolean admin;

	public LoginRes() {
		super();
	}

	public LoginRes(int code, String message) {
		super(code, message);
	}

	public LoginRes(int code, String message, String account, boolean admin) {
		super(code, message);
		this.account = account;
		this.admin = admin;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public boolean isAdmin() {
		return admin;
	}

	public void setAdmin(boolean admin) {
		this.admin = admin;
	}
	
}
