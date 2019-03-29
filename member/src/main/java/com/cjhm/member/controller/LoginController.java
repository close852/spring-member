package com.cjhm.member.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cjhm.annotation.SocialUser;
import com.cjhm.member.constants.MemberConstants;
import com.cjhm.member.entity.User;
import com.cjhm.member.enums.SocialType;

@Controller
public class LoginController {

	/**
	 * 아래꺼를 직접 구현...
	 * @param user
	 * @return
	 */
//	@GetMapping(value ="/member/{facebook|google|kakao}/complete")
//	public String socialLoginComplete(HttpSession session, OAuth2Authentication authentication) {
//		if(authentication==null) {
//			//오류처리 해야함....
//			return "redirect:/";
//		}
//		Map<String, Object> map =null;
//		if(authentication!=null) {
//			map = (HashMap<String, Object>) authentication.getUserAuthentication().getDetails();
//		}
////		for (String str : map.keySet()) {
////			System.out.println(str + " :: " + map.get(str));
////		} 
//		User user = new User();
//		user.setEmail(String.valueOf(map.get("email")));
//		user.setName(String.valueOf(map.get("name")));
//		user.setPrincipal(String.valueOf(map.get("id")));
//		user.setSocialType(SocialType.FACEBOOK);
//		user.setCreateDate(); 
//		// builder 생성 
//		session.setAttribute(MemberConstants.SESSION_USER, user);
//		return "redirect:/";
//	}
	/**
	 * UserArgumentResolver가 역할을 수행함
	 * 
	 * @param user
	 * @return
	 */
	@GetMapping(value ="/member/{facebook|google|kakao}/complete")
	public String socialLoginComplete(@SocialUser User user) {
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
