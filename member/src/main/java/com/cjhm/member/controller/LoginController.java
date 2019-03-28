package com.cjhm.member.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cjhm.member.constants.MemberConstants;
import com.cjhm.member.entity.User;
import com.cjhm.member.enums.SocialType;

@Controller
public class LoginController {

	@GetMapping(value ="/member/{facebook|google|kakao}/complete")
	public String socialLoginComplete(HttpSession session) {
		System.err.println("socialLoginComplete ~");
		OAuth2Authentication authentication = (OAuth2Authentication) SecurityContextHolder.getContext().getAuthentication();
		Map<String, String> map = (HashMap<String, String>) authentication.getUserAuthentication().getDetails();
		for (String str : map.keySet()) {
			System.out.println(str + " :: " + map.get(str));
		}
		User user = new User();
		user.setEmail(map.get("email"));
		user.setName(map.get("name"));
		user.setPrincipal(map.get("id"));
		user.setSocialType(SocialType.FACEBOOK);
		user.setCreateDate();
		// builder 생성
		session.setAttribute(MemberConstants.SESSION_USER, user);
		return "redirect:/";
	}
//	@GetMapping(value ="/{facebook|google|kakao}/complete")
//	public String socialLoginComplete(@SocialUser User user) {
//		OAuth2Authentication authentication = (OAuth2Authentication) SecurityContextHolder.getContext().getAuthentication();
//		Map<String, String> map = (HashMap<String, String>) authentication.getUserAuthentication().getDetails();
//		for (String str : map.keySet()) {
//			System.out.println(str + " :: " + map.get(str));
//		}
//		User user = new User();
//		user.setEmail(map.get("email"));
//		user.setName(map.get("name"));
//		user.setPrincipal(map.get("id"));
//		user.setSocialType(SocialType.FACEBOOK);
//		user.setCreateDate();
//		// builder 생성
//		session.setAttribute(MemberConstants.SESSION_USER, user);
//		return "redirect:/";
//	}
	
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
