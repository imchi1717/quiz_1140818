package com.example.quiz_1140818.request;

import com.example.quiz_1140818.constants.ConstantsMessage;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class RegisterReq extends BasicReq {

	@NotBlank(message = ConstantsMessage.USER_NAME_ERROR)
	private String name;

	private String phone;

	@NotBlank(message = ConstantsMessage.USER_MAIL_ERROR)
	private String email;

	@Min(value = 12, message = ConstantsMessage.USER_AGE_ERROR)
	private int age;

	

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
