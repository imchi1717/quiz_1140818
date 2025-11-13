package com.example.quiz_1140818.request;

import java.util.List;

import com.example.quiz_1140818.constants.ConstantsMessage;
import com.example.quiz_1140818.entity.User;
import com.example.quiz_1140818.vo.Answer;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public class FillinReq {
	
	@NotNull(message = ConstantsMessage.USER_INFO_IS_NULL)
	@Valid // 嵌套驗證用: 為了讓類別 User 中的屬性限制生效
	private User user;
	
	@NotNull(message = ConstantsMessage.QUIZ_ID_ERROR)
	private int quizId;
	
	@Valid
	private List<Answer> answerList;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public int getQuizId() {
		return quizId;
	}

	public void setQuizId(int quizId) {
		this.quizId = quizId;
	}

	public List<Answer> getAnswerList() {
		return answerList;
	}

	public void setAnswerList(List<Answer> answerList) {
		this.answerList = answerList;
	}
	
	

}
