package com.example.quiz_1140818.entity;

import com.example.quiz_1140818.constants.ConstantsMessage;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "account")
public class User {

	
	@NotBlank(message = ConstantsMessage.USER_NAME_ERROR)
	@Column(name = "name")
	private String name;

	@Column(name = "phone")
	private String phone;

	@NotBlank(message = ConstantsMessage.USER_MAIL_ERROR)
	@Id
	@Column(name = "email")
	private String email;

	@Min(value = 1, message = ConstantsMessage.USER_AGE_ERROR)
	@Column(name = "age")
	private int age;

	public User() {
		super();
	}

	public User( String name, String phone, String email, int age) {
		super();
		this.name = name;
		this.phone = phone;
		this.email = email;
		this.age = age;
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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}


}
