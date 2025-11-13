package com.example.quiz_1140818.constants;

public enum ResCodeMessage {
	
	SUCCESS(200, "Success!!"), //
	ADD_INFO_FAILED(400, "Add info failed!!"), //
	UPDATE_INFO_FAILED(400, "Update info failed!!"), //
	NOT_FOUND(404, "Not found!!"), //
	PARAM_ACCOUNT_ERROR(400, "Param account error!!"), //
	PARAM_PASSWORD_ERROR(400, "Param password error!!"), //
	PASSWORD_MISMATCH(400, "Password mismatch!!"), //
	ACCOUNT_EXISTS(400, "Account exist"), //
	QUESTION_TYPE_ERROR(400, ConstantsMessage.QUESTION_TYPE_ERROR), //
	QUESTION_TYPE_OPTIONS_MISMATCH(400, "Question type and options mismatch!!"), //
	QUIZ_DATE_ERROR(400, "Quiz date error!!"), //
	QUIZ_ID_ERROR(400, "Quiz id error!!"), //
	RADIO_ANSWER_IS_REQUIRED(400, "Radio answer is required!!"), //
	TEXT_ANSWER_IS_REQUIRED(400, "Text answer is required!!"), //
	CHECKBOX_ANSWER_IS_REQUIRED(400, "Checkbox answer is required!!"), // 
	QUESTION_OPTION_MISMATCH(400, "Question option mismatch!!");
	
	private int code;

	private String message;

	private ResCodeMessage(int code, String message) {
		this.code = code;
		this.message = message;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
