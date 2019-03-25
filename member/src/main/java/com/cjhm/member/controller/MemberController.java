package com.cjhm.member.controller;

import java.util.UUID;

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
import org.springframework.web.bind.annotation.ResponseBody;

import com.cjhm.member.constants.MemberConstants;
import com.cjhm.member.entity.User;
import com.cjhm.member.service.MemberService;
import com.cjhm.system.mail.service.EmailSender;

@Controller
@RequestMapping("/member")
public class MemberController {

	@Autowired
	MemberService memberService;
	
	@Autowired
	EmailSender emailSender;

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
	public String postLogin(@RequestParam("email") String email, @RequestParam("password") String password, Model model,
			HttpSession session) {
		User u = (User) session.getAttribute(MemberConstants.SESSION_USER);
		if (u != null) {
			session.invalidate();
		}
		u = memberService.login(email, password);
		//로그인 후 처리 로직
		//내부,외부 여부
		//사용자 정보 쿠키저장
		//사용자 토큰 생성
		//암호 만료 여부 확인
		//중복 로그인 관련 설정
		//권한 설정
		//세션 정보 저장
		
		//로그인 후 처리 로직
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

	@GetMapping("/password_reset")
	public String passwordReset() {
		return "/member/passwordReset";
	}
	@PostMapping("/password_reset")
	public String postPasswordReset(@RequestParam("email") String email,Model model) {
		User u = memberService.findUserByEmail(email);
		if(u==null) {
			model.addAttribute("error", "Can't find that email, sorry.");
			return "/member/passwordReset";
		}else {
			String tempPassword=UUID.randomUUID().toString().replaceAll("-", "").substring(16);
			u.setPassword(tempPassword);
			memberService.updateUserByEmail(u);
			emailSender.emailSender(email, tempPassword);
			//Async로 처리할 방법을 찾아야 한다...
//			SendMailThread smtpThread = new SendMailThread(email,tempPassword);
//			smtpThread.run();
			return "redirect:/member/login";
		}
	}
	
	@GetMapping("/logout")
	public String logout(HttpSession session) {
		session.invalidate();
		return "redirect:/";
	}
	
	public static void main(String[] args) {
		System.out.println(UUID.randomUUID().toString().replaceAll("-", "").substring(16));
	}
}
