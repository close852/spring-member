package com.cjhm.member.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cjhm.annotation.SocialUser;
import com.cjhm.member.entity.User;

@Controller
public class LoginController {

	/**
	 * UserArgumentResolver가 역할을 수행함
	 * 
	 * @param user
	 * @return
	 */
	
	
	@GetMapping("/loginSuccess")
	public String loginSuccess(@SocialUser User user) {
		return "redirect:/";
	}
	@PostMapping("/loginSuccess")
	public String postloginSuccess(@SocialUser User user) {
		return "redirect:/";
	}
	@GetMapping("/loginFailure")
	public String loginFailure() {
		return "redirect:/";
	}
	
	@GetMapping("/facebook")
	@ResponseBody
	public String facebook() {
		return "facebook";
	}
	@GetMapping("/kakao")
	@ResponseBody
	public String kakao() {
		return "kakao";
	}
	@GetMapping("/google")
	@ResponseBody
	public String google() {
		return "google";
	}
}
