package com.example.quiz_1140818.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.example.quiz_1140818.constants.QuestionType;
import com.example.quiz_1140818.constants.ResCodeMessage;
import com.example.quiz_1140818.dao.AccountDao;
import com.example.quiz_1140818.dao.FillinDao;
import com.example.quiz_1140818.dao.QuestionDao;
import com.example.quiz_1140818.dao.QuizDao;
import com.example.quiz_1140818.entity.Fillin;
import com.example.quiz_1140818.entity.Question;
import com.example.quiz_1140818.entity.Quiz;
import com.example.quiz_1140818.entity.User;
import com.example.quiz_1140818.response.BasicRes;
import com.example.quiz_1140818.response.FeedbackRes;
import com.example.quiz_1140818.response.StatisticRes;
import com.example.quiz_1140818.vo.Answer;
import com.example.quiz_1140818.vo.FeedbackVo;
import com.example.quiz_1140818.vo.Options;
import com.example.quiz_1140818.vo.OptionsCount;
import com.example.quiz_1140818.vo.QuestionAnswerVo;
import com.example.quiz_1140818.vo.QuestionCountVo;
import com.example.quiz_1140818.vo.StatisticVo;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class FillinService {

	private ObjectMapper mapper = new ObjectMapper();

	@Autowired
	private FillinDao fillinDao;

	@Autowired
	private QuestionDao questionDao;

	@Autowired
	private QuizDao quizDao;

	@Autowired
	private AccountDao accountDao;

	@Transactional(rollbackFor = Exception.class)
	public BasicRes fillin(User user, int quizId, List<Answer> answerList) throws Exception {
		// 要檢查答案，檢查的基準就是存在 DB 中同張問卷的那些問題
		// 1. 取出同張問卷的所有問題
		List<Question> questionList = questionDao.getByQuizId(quizId);

		for (Question question : questionList) {
			// 2. 檢查是否都有答案
			if (question.isRequired()) {
				BasicRes res = checkRequiredAnswer(question.getQuestionId(), answerList, question.getType());
				if (res != null) {
					return res;
				}
			}

			// 3. 答案中的選項是否跟問卷中的選項一樣
			List<Options> reqOptionList = new ArrayList<>();
			for (Answer answer : answerList) {
				if (question.getQuestionId() == answer.getQuestionId()) {
					reqOptionList = answer.getOptionsList();
					break;
				}
			}
			try {
				BasicRes res = checkOptions(question.getOptionsStr(), reqOptionList);
				if (res != null) {
					return res;
				}
			} catch (Exception e) {
				throw e;
			}
		}
		// 4. 寫答案
		try {
			for (Answer item : answerList) {
				fillinDao.fillin(quizId, item.getQuestionId(), user.getEmail(), user.getName(), user.getPhone(),
						user.getAge(), mapper.writeValueAsString(item), LocalDate.now());
			}
		} catch (Exception e) {
			throw e;
		}
		return new BasicRes(ResCodeMessage.SUCCESS.getCode(), //
				ResCodeMessage.SUCCESS.getMessage());
	}

	private BasicRes checkRequiredAnswer(int questionId, List<Answer> answerList, String type) {
		for (Answer answer : answerList) {
			// 比對相同問題編號，確認問題型態所對應的 answer 是否有值
			if (answer.getQuestionId() == questionId) {
				if (type.equalsIgnoreCase(QuestionType.SINGLE.getType())) {
					if (answer.getRadioAnswer() <= 0) {
						return new BasicRes(ResCodeMessage.RADIO_ANSWER_IS_REQUIRED.getCode(), //
								ResCodeMessage.RADIO_ANSWER_IS_REQUIRED.getMessage());
					}
				} else if (type.equalsIgnoreCase(QuestionType.TEXT.getType())) { // +++ 修正: 這裡應為 TEXT +++
					if (!StringUtils.hasText(answer.getTextAnswer())) {
						return new BasicRes(ResCodeMessage.TEXT_ANSWER_IS_REQUIRED.getCode(), //
								ResCodeMessage.TEXT_ANSWER_IS_REQUIRED.getMessage());
					}
				} else { // 問題型態是多選
					if (answer.getOptionsList() == null || answer.getOptionsList().isEmpty()) {
						// 如果多選題的答案列表是空的，直接回傳錯誤
						return new BasicRes(ResCodeMessage.CHECKBOX_ANSWER_IS_REQUIRED.getCode(), //
								ResCodeMessage.CHECKBOX_ANSWER_IS_REQUIRED.getMessage());
					}
					for (Options item : answer.getOptionsList()) {
						// 檢查至少有一個checkBoolean 的值是 true
						if (item.isCheckBoolean()) {
							return null; // 有勾選，通過
						}
					}
					// +++ 修正: return 應在迴圈外 +++
					// 迴圈跑完都沒找到 true，表示未填答
					return new BasicRes(ResCodeMessage.CHECKBOX_ANSWER_IS_REQUIRED.getCode(), //
							ResCodeMessage.CHECKBOX_ANSWER_IS_REQUIRED.getMessage());
				}
			}
		}
		return null; // 預設通過
	}

	private BasicRes checkOptions(String optionsStr, List<Options> reqOptionsList) throws Exception {
		// 轉換 optionsStr 成物件 List<Options>
		try {
			List<Options> optionsList = mapper.readValue(optionsStr, new TypeReference<>() {
			});
			for (Options item : optionsList) {
				int code = item.getCode();
				String optionName = item.getOptionName();
				for (Options reqItem : reqOptionsList) {
					// 相同編號下，若選像不一樣則回傳錯誤
					if (code == reqItem.getCode()) {
						if (!optionName.equalsIgnoreCase(reqItem.getOptionName())) {
							return new BasicRes(ResCodeMessage.QUESTION_OPTION_MISMATCH.getCode(), //
									ResCodeMessage.QUESTION_OPTION_MISMATCH.getMessage());
						}
					}
				}
			}
		} catch (Exception e) {
			throw e;
		}
		return null;
	}

	public FeedbackRes feedback(int quizId) throws Exception {
		if (quizId <= 0) {
			return new FeedbackRes(ResCodeMessage.QUIZ_ID_ERROR.getCode(), //
					ResCodeMessage.QUIZ_ID_ERROR.getMessage());
		}
		List<Question> questionList = questionDao.getByQuizId(quizId);

		// +++ 最小修改 1: 將 Question 實體存入 map +++
		// 將 Question 轉成 QuestionAnswerVo (X) -> 將 Question 實體存入 map (O)
		Map<Integer, Question> map = new HashMap<>();
		for (Question question : questionList) {
			// 問題編號，Question 實體
			map.put(question.getQuestionId(), question);
		}
		// +++ 修改 1 結束 +++

		Quiz quiz = quizDao.getById(quizId);

		List<Fillin> fillinList = fillinDao.getByQuizId(quizId);
		// ====================================
		// 一個 email 表示一位使用者的 FeedbackVo
		Map<String, FeedbackVo> emailFeedbackVoMap = new HashMap<>();
		for (Fillin item : fillinList) {
			FeedbackVo feedbackVo = new FeedbackVo();
			List<QuestionAnswerVo> questionAnswerVoList = new ArrayList<>();
			String email = item.getEmail();
			if (!emailFeedbackVoMap.containsKey(email)) { // 表示尚未記錄到該 user 的答案
				User user = new User(item.getName(), item.getPhone(), item.getEmail(), //
						item.getAge());
				// 將 User、Quiz、QuestionVoList、FillinDate 設定到 feedbackVo
				feedbackVo.setUser(user);
				feedbackVo.setQuiz(quiz);
				feedbackVo.setFillinDate(item.getFillinDate());
				feedbackVo.setQuestionVoList(questionAnswerVoList);
				emailFeedbackVoMap.put(email, feedbackVo);
			} else {
				feedbackVo = emailFeedbackVoMap.get(email);
				questionAnswerVoList = feedbackVo.getQuestionVoList();
			}

			try {
				Answer ans = mapper.readValue(item.getAnswerStr(), Answer.class);

				// +++ 最小修改 2: 組合 Question 和 Answer 來建立「新的」Vo +++

				// 1. 透過 questionId 從 map 取得「問題」實體
				Question question = map.get(item.getQuestionId());
				if (question == null)
					continue; // 安全檢查

				// 2. 建立一個「新的」Vo，避免共用 Bug
				QuestionAnswerVo vo = new QuestionAnswerVo(question.getQuizId(), //
						question.getQuestionId(), question.getName(), //
						question.getType(), question.isRequired());

				// 3. 設定「答案」
				vo.setTextAnswer(ans.getTextAnswer());
				vo.setRadioAnswer(ans.getRadioAnswer());

				// 4. 根據「題型」設定「選項列表」
				String type = question.getType();

				// 假設 Question entity 有 .getOptions() 方法取得 JSON string
				String optionsStr = question.getOptionsStr();

				if (type.equalsIgnoreCase(QuestionType.SINGLE.getType())) {
					// 單選：我們需要「問題」的完整選項列表
					if (StringUtils.hasText(optionsStr)) {
						List<Options> optionsList = mapper.readValue(optionsStr, new TypeReference<>() {
						});
						vo.setOptionsList(optionsList);
					}
				} else if (type.equalsIgnoreCase(QuestionType.MULTI.getType())) {
					// 多選：我們需要「問題」的選項 + 「答案」的勾選狀態
					if (StringUtils.hasText(optionsStr)) {
						List<Options> optionsList = mapper.readValue(optionsStr, new TypeReference<>() {
						});

						// 建立答案 Map (key: code, value: true)
						Map<Integer, Boolean> answerCheckMap = new HashMap<>();
						if (ans.getOptionsList() != null) {
							for (Options ansOption : ans.getOptionsList()) {
								if (ansOption.isCheckBoolean()) {
									answerCheckMap.put(ansOption.getCode(), true);
								}
							}
						}

						// 合併勾選狀態
						for (Options fullOption : optionsList) {
							if (answerCheckMap.containsKey(fullOption.getCode())) {
								fullOption.setCheckBoolean(true);
							} else {
								fullOption.setCheckBoolean(false);
							}
						}
						vo.setOptionsList(optionsList);
					}
				}
				// 文字題('T')：不需要 optionsList，保持 null 即可

				questionAnswerVoList.add(vo);
				// +++ 修改 2 結束 +++

			} catch (Exception e) {
				throw e;
			}

		}

		// 將 emailFeedbackVoMap 的 FeedbackVo 增加到 feedbackVoList
		List<FeedbackVo> feedbackVoList = new ArrayList<>(emailFeedbackVoMap.values());

		return new FeedbackRes(ResCodeMessage.SUCCESS.getCode(), //
				ResCodeMessage.SUCCESS.getMessage(), feedbackVoList);
	}

	public StatisticRes statistic(int quizId) throws Exception {
		if (quizId <= 0) {
			return new StatisticRes(ResCodeMessage.QUIZ_ID_ERROR.getCode(), //
					ResCodeMessage.QUIZ_ID_ERROR.getMessage());
		}
		// 將問題相關的資訊設定給 QuestionAnswerVo
		// questionId, QuestionCountVo
		Map<Integer, QuestionCountVo> voMap = setQuestionAnswerVo(quizId);
		// 使用 QuizId 撈取所有的填答
		List<Fillin> fillinList = fillinDao.getByQuizId(quizId);
		// 問題編號, 選項編號 選項 次數
		Map<Integer, Map<Integer, Map<String, Integer>>> map = new HashMap<>();
		for (Fillin fillin : fillinList) {
			try {
				// 1. 把 answer_str 轉成 Answer
				Answer ans = mapper.readValue(fillin.getAnswerStr(), Answer.class);
				// 2. 統計次數
				// 2.1 簡答題
				if (StringUtils.hasText(ans.getTextAnswer())) {
					// textAnswer 有內容的話，表示該題是簡答題 --> 跳過
					continue;
				}
				// 選項編號, 選項, 次數
				Map<Integer, Map<String, Integer>> codeOpCountMap = new HashMap<>();
				if (map.containsKey(ans.getQuestionId())) {
					// 若問題編號已存在，則把對應的 選項編號、選項、次數的 Map 取出
					codeOpCountMap = map.get(ans.getQuestionId());
				}
				// 2.2 多選題: 先做的原因是因為要先取得選項編號與選項，而其答案是綁定在 List<Options> 中
				// 可以順便蒐集次數
				for (Options op : ans.getOptionsList()) {
					// 先判斷 opCountMap 中是否已有蒐集過的選項編號
					if (codeOpCountMap.containsKey(op.getCode())) {
						// 有蒐集過的選項編號
						// 判斷 checkBoolean 的值是否為 true
						if (op.isCheckBoolean()) {
							// --> 取出對應的 value (選項和次數的 map)
							// 選項, 次數
							Map<String, Integer> opCountMap = codeOpCountMap.get(op.getCode());
							// --> 取出選項對應的次數後再 + 1
							int count = opCountMap.get(op.getOptionName()) + 1;
							// --> 將更新後的次數放回(put) opCountMap
							opCountMap.put(op.getOptionName(), count);
							// codeOpCountMap 不需要更新，因為其對應 value 的記憶體上的值(opCountMap)已更新
						}
					} else {
						// 沒有蒐集過的選項編號 --> 建立新的, 次數是 0
						Map<String, Integer> opCountMap = new HashMap<>();
						int count = 0;
						// checkBoolean 的值是否為 true
						if (op.isCheckBoolean()) {
							// 有的話 --> 次數變成 1
							count = 1;
						}
						opCountMap.put(op.getOptionName(), count);
						// 將結果更新回 codeOpCountMap
						codeOpCountMap.put(op.getCode(), opCountMap);
					}
				}
				// 至此選項編號和選項已蒐集完畢
				// 2.3 單選題
				if (ans.getRadioAnswer() > 0) {
					int radioCode = ans.getRadioAnswer();
					// **A. 檢查統計 Map 中是否已存在該選項編號的結構 (多選題邏輯沒有建立時)**
					if (!codeOpCountMap.containsKey(radioCode)) {

						// 從 voMap 取得包含選項清單的 QuestionCountVo
						QuestionCountVo questionVo = voMap.get(ans.getQuestionId());

						if (questionVo != null && questionVo.getOptionsList() != null) {

							// 查找 radioCode 對應的選項名稱 (opName)
							String radioOptionName = questionVo.getOptionsList().stream()
									.filter(op -> op.getCode() == radioCode).map(Options::getOptionName).findFirst()
									.orElse(null);

							// **B. 初始化計數結構**
							if (radioOptionName != null) {
								Map<String, Integer> opCountMap = new HashMap<>();
								opCountMap.put(radioOptionName, 0);
								codeOpCountMap.put(radioCode, opCountMap);
							}
						}
					}

					// 根據選項編號從 Map<選項編號,Map<選項,次數>> 中取出對應的 Map<選項,次數>
					Map<String, Integer> opCountMap = codeOpCountMap.get(ans.getRadioAnswer());

					if (opCountMap != null) {
						// 更新次數
						for (String optionName : opCountMap.keySet()) {
							// opCountMap 中只會有一筆資料而已，因為一個選項編號下，只會有一個選項和一個次數
							int count = opCountMap.get(optionName) + 1;
							opCountMap.put(optionName, count);
						}
					}
				}
				map.put(ans.getQuestionId(), codeOpCountMap);
			} catch (Exception e) {
				throw e;
			}
		}
		// 將每一題中每個編號的選項和次數設定回 QuestionCountVo
		List<QuestionCountVo> voList = setAndGetQuestionCountVoList(map, voMap);
		Quiz quiz = quizDao.getById(quizId);
		StatisticVo statisticVo = new StatisticVo(quiz, voList);
		return new StatisticRes(ResCodeMessage.SUCCESS.getCode(), //
				ResCodeMessage.SUCCESS.getMessage(), statisticVo);
	}

	// 將 Question 的所有選擇題的基本資訊(不包含選項和次數)設定到 QuestionCountVo
	private Map<Integer, QuestionCountVo> setQuestionAnswerVo(int quizId) throws Exception{
		// 將問題相關的資訊設定給 QuestionAnswerVo
		List<Question> questionList = questionDao.getByQuizId(quizId);
		// 將 Question 轉成 QuestionAnswerVo
		Map<Integer, QuestionCountVo> map = new HashMap<>();
		for (Question question : questionList) {
			QuestionCountVo vo = new QuestionCountVo(//
					question.getQuestionId(), question.getName(), //
					question.getType(), question.isRequired());

			// **使用新的 setOptionsList 方法**
			if (StringUtils.hasText(question.getOptionsStr())) {
				List<Options> options = mapper.readValue(question.getOptionsStr(), new TypeReference<List<Options>>() {
				});
				vo.setOptionsList(options); // <--- 關鍵：將選項清單儲存到 vo 中
			}
			// 問題編號，vo
			map.put(question.getQuestionId(), vo);
		}
		return map;
	}

	private List<QuestionCountVo> setAndGetQuestionCountVoList(Map<Integer, Map<Integer, Map<String, Integer>>> map, //
			Map<Integer, QuestionCountVo> voMap) {
		List<QuestionCountVo> voList = new ArrayList<>();
		for (int questionId : map.keySet()) {
			List<OptionsCount> opCountList = new ArrayList<>();
			// 取出對應的 Map<選項編號, Map<選項, 次數>>
			Map<Integer, Map<String, Integer>> codeOpCountMap = map.get(questionId);

			// **新增檢查：確保 codeOpCountMap 不是 null**
			if (codeOpCountMap == null) {
				continue; // 跳過這個問題ID，避免當機
			}

			// 以下2種寫法擇一
			// 寫法1
			for (int code : codeOpCountMap.keySet()) {
				Map<String, Integer> opNameCountMap = codeOpCountMap.get(code);
				for (String opName : opNameCountMap.keySet()) {
					int count = opNameCountMap.get(opName);
					OptionsCount opCount = new OptionsCount(code, opName, count);
					opCountList.add(opCount);
				}

			}
			// 寫法2: 以下是 Lambda 寫法: 執行效率有比上面的程式碼好
//			codeOpCountMap.forEach((code, v) -> {
//				v.forEach((opName, count) -> {
//					OptionsCount opCount = new OptionsCount(code, opName, count);
//					opCountList.add(opCount);
//				});
//			});
			// voMap 是之前先整理過的 Map<問題編號, QuestionCountVo>，所以所有選擇題都會有
			QuestionCountVo vo = voMap.get(questionId);
			vo.setOptionsCountList(opCountList);
			voList.add(vo);
		}
		return voList;
	}

}