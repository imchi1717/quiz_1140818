package com.example.quiz_1140818.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.quiz_1140818.request.BasicReq;
import com.example.quiz_1140818.request.RegisterReq;
import com.example.quiz_1140818.response.AccountRes;
import com.example.quiz_1140818.response.BasicRes;
import com.example.quiz_1140818.response.LoginRes;
import com.example.quiz_1140818.response.RegisterRes;
import com.example.quiz_1140818.service.AccountService;

import jakarta.validation.Valid;

@CrossOrigin
@RestController
public class AccountController {

	@Autowired
	private AccountService accountService;
	
//	@PostMapping(value = "quiz/add_info")
//	public BasicRes addInfo(@Valid @RequestBody BasicReq req) {
//		return accountService.addInfo(req.getAccount(), req.getPassword(), req.isAdmin());
//	}
	
	@PostMapping(value = "quiz/register")
	public BasicRes register(@Valid @RequestBody RegisterReq req) {
		return accountService.addInfo(req.getAccount(), req.getPassword(), req.isAdmin(),
				 req.getName(), req.getPhone(), req.getEmail(), req.getAge());
	}
	
	@PostMapping(value = "quiz/login")
	public LoginRes login(@Valid @RequestBody BasicReq req) {
		return accountService.login(req.getAccount(), req.getPassword());
	}
	
	@PostMapping(value = "quiz/accountLogin")
	public AccountRes accountLogin(@Valid @RequestBody BasicReq req) {
		return accountService.accountLogin(req.getAccount(), req.getPassword());
	}
	
}
