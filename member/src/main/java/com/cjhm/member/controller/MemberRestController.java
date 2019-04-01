package com.cjhm.member.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cjhm.member.entity.User;
import com.cjhm.member.service.MemberService;
import com.google.gson.Gson;

@RestController
@RequestMapping("/apis/member")
public class MemberRestController {

	Logger logger = LoggerFactory.getLogger(MemberRestController.class);

	@Autowired
	MemberService memberService;

	@PostMapping("/findUserByIdAjax")
	public String findUserByIdAjax(@RequestParam("email") String email) {
		logger.error("findUserByIdAjax : " + email);
		User u = memberService.findAuthUserByEmail(email,null);
		Gson gson = new Gson();
		String json = gson.toJson(u);
		logger.error(json);
		if (u == null) {
			json = "";
		}
		return json;
	}
}
