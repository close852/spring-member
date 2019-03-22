package com.cjhm.member.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.cjhm.member.entity.User;
import com.cjhm.member.service.MemberService;

@Controller
@RequestMapping("/member")
public class MerberController {

	@Autowired
	MemberService memberService;

	@GetMapping("/login")
	public String getLogin() {
		return "member/login";
	}

	@GetMapping("/login2")
	public String getLogin2() {
		return "member/login2";
	}

	@PostMapping("/login")
	public String postLogin(@RequestParam("loginid") String loginid, @RequestParam("password") String password,
			Model model) {
		User u = memberService.login(loginid, password);

		// error 메시지 전달
		if (u == null) {
			model.addAttribute("error", "Incorrect email or password.");
			return "member/login";
		}
		return "redirect:/";
	}
}
