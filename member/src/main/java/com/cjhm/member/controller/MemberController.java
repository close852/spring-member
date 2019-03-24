package com.cjhm.member.controller;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.cjhm.member.constants.MemberConstants;
import com.cjhm.member.entity.User;
import com.cjhm.member.service.MemberService;

@Controller
@RequestMapping("/member")
public class MemberController {

	@Autowired
	MemberService memberService;
	Logger logger = LoggerFactory.getLogger(MemberController.class);

	@GetMapping("/login")
	public String getLogin(HttpSession session) {
		User u = (User) session.getAttribute(MemberConstants.SESSION_USER);
		if (u != null) {
			return "redirect:/";
		}
		return "member/login";
	}

	@PostMapping("/login")
	public String postLogin(@RequestParam("email") String email, @RequestParam("password") String password,
			Model model, HttpSession session) {
		User u = (User) session.getAttribute(MemberConstants.SESSION_USER);
		if (u != null) {
			session.invalidate();
		}
		u = memberService.login(email, password);
		session.setAttribute(MemberConstants.SESSION_USER, u);
		// error 메시지 전달
		if (u == null) {
			model.addAttribute("error", "Incorrect email or password.");
			return "/member/login";
		}
		return "redirect:/";
	}

	@GetMapping("/join")
	public String join() {
		return "member/join";
	}

	@PostMapping("/join")
	public String postJoin(@RequestParam("username") String name, @RequestParam("password") String password,
			@RequestParam("email") String email) {
		User u = new User();
		u.setName(name);
		u.setEmail(email);
		u.setPassword(password);

		logger.error("user data : " + u);
		u = memberService.saveUser(u);
		logger.error("user save data : " + u);
		if (u != null) {
			return "redirect:/";
		} else {
			return "redirect:/member/join";
		}
	}
	@GetMapping("/logout")
	public String logout(HttpSession session) {
		session.invalidate();
		return "redirect:/";
	}
}
