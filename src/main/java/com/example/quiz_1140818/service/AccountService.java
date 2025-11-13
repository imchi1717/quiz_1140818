package com.example.quiz_1140818.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.quiz_1140818.constants.ResCodeMessage;
import com.example.quiz_1140818.dao.AccountDao;
import com.example.quiz_1140818.entity.Account;
import com.example.quiz_1140818.response.AccountRes;
import com.example.quiz_1140818.response.BasicRes;
import com.example.quiz_1140818.response.LoginRes;

@Service
public class AccountService {
	
	// BCrypt 密碼加密器
	private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

	@Autowired
	public AccountDao dao;

	public BasicRes addInfo(String account, String password, boolean isAdmin, String name, String phone,
			String email, int age) {
		try {
			// 若文件有說明在新增資訊之前要先檢查帳號是否已存在
			int count = dao.selectCountByAccount(account);
			// 因為是透過 PK 欄位 account 來查詢是否有存在值，所以 count 只會是 0 或 1 
			if(count == 1) {
				return new BasicRes(ResCodeMessage.ACCOUNT_EXISTS.getCode(), //
						ResCodeMessage.ACCOUNT_EXISTS.getMessage());
			}
			// 存進 DB 中的密碼要記得加密
			dao.addInfo(account, encoder.encode(password), isAdmin, name, phone, email, age);
			return new BasicRes(ResCodeMessage.SUCCESS.getCode(), //
					ResCodeMessage.SUCCESS.getMessage());
		} catch (Exception e) {
			// 若是 id(PK) 已存在，新增資料就會失敗
			// 發生 Exception 時，可以有以下2種處理方式:
			// 1. 固定的回覆訊息，但真正錯誤原因無法顯示
//			return new BasicRes(ResCodeMessage.ADD_INFO_FAILED.getCode(), //
//			ResCodeMessage.ADD_INFO_FAILED.getMessage());
			
			// 2. 將 catch 到的例外(Exception)拋出(throw)，再由自定義的類別
			//    GlobalExceptionHandler 寫入(回覆)真正的錯誤訊息
			throw e;
		}
	}

	
	public LoginRes login(String account, String password) {
		// 使用 account 取得對應資料
		Account data = dao.selectByAccount(account);
		if(data == null) {   // data == null 表示沒資料 --> 也表示該帳號不存在
			return new LoginRes(ResCodeMessage.NOT_FOUND.getCode(), //
					ResCodeMessage.NOT_FOUND.getMessage());
		}
		// 比對密碼:使用排除法，所以前面記得要有 ! ，表示匹配不成功
		if(!encoder.matches(password, data.getPassword())) {
			return new LoginRes(ResCodeMessage.PASSWORD_MISMATCH.getCode(), //
					ResCodeMessage.PASSWORD_MISMATCH.getMessage());
		}
		return new LoginRes(ResCodeMessage.SUCCESS.getCode(), //
				ResCodeMessage.SUCCESS.getMessage(), account, data.isAdmin());
	}
	
	public AccountRes accountLogin(String account, String password) {
		// 使用 account 取得對應資料
		Account data = dao.selectByAccount(account);
		if(data == null) {   // data == null 表示沒資料 --> 也表示該帳號不存在
			return new AccountRes(ResCodeMessage.NOT_FOUND.getCode(), //
					ResCodeMessage.NOT_FOUND.getMessage());
		}
		// 比對密碼:使用排除法，所以前面記得要有 ! ，表示匹配不成功
		if(!encoder.matches(password, data.getPassword())) {
			return new AccountRes(ResCodeMessage.PASSWORD_MISMATCH.getCode(), //
					ResCodeMessage.PASSWORD_MISMATCH.getMessage());
		}
		return new AccountRes(ResCodeMessage.SUCCESS.getCode(), //
				 ResCodeMessage.SUCCESS.getMessage(), data.getAccount(), //
				  data.getEmail(), data.getName(), data.getPhone(), data.getAge(), data.isAdmin());
	}
}
