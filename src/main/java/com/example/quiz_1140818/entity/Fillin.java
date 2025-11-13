package com.example.quiz_1140818.entity;

import java.time.LocalDate;

import com.example.quiz_1140818.constants.ConstantsMessage;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

@Entity
@IdClass(value = FillinId.class)
@Table(name = "fillin")
public class Fillin {

	@Id
	@Column(name = "quiz_id")
	private int quizId;

	@Id
	@Column(name = "question_id")
	private int questionId;

	@Id
	@Column(name = "email")
	private String email;

	@NotBlank(message = ConstantsMessage.USER_NAME_ERROR)
	@Column(name = "name")
	private String name;

	@Column(name = "phone")
	private String phone;

	@Min(value = 1, message = ConstantsMessage.USER_AGE_ERROR)
	@Column(name = "age")
	private int age;

	// 物件 Answer 轉成的字串
	@Column(name = "answer_str")
	private String answerStr;

	@Column(name = "fillin_date")
	private LocalDate fillinDate;

	public int getQuizId() {
		return quizId;
	}

	public void setQuizId(int quizId) {
		this.quizId = quizId;
	}

	public int getQuestionId() {
		return questionId;
	}

	public void setQuestionId(int questionId) {
		this.questionId = questionId;
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

	public String getAnswerStr() {
		return answerStr;
	}

	public void setAnswerStr(String answerStr) {
		this.answerStr = answerStr;
	}

	public LocalDate getFillinDate() {
		return fillinDate;
	}

	public void setFillinDate(LocalDate fillinDate) {
		this.fillinDate = fillinDate;
	}

}
