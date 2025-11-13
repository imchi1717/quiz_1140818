package com.example.quiz_1140818.response;

import java.util.List;

import com.example.quiz_1140818.vo.QuestionVo;

public class QuestionListRes extends BasicRes{
	
	private List<QuestionVo> questionVoList;

	public QuestionListRes() {
		super();
	}

	public QuestionListRes(int code, String message) {
		super(code, message);
	}

	public QuestionListRes(int code, String message, List<QuestionVo> questionVoList) {
		super(code, message);
		this.questionVoList = questionVoList;
	}

	public List<QuestionVo> getQuestionVoList() {
		return questionVoList;
	}

	public void setQuestionVoList(List<QuestionVo> questionVoList) {
		this.questionVoList = questionVoList;
	}
	
}
