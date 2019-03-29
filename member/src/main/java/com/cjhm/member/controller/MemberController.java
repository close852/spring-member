package com.cjhm.member.controller;

import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
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
	public String getLogin() {
		return "/member/login";
	}

	@PostMapping("/login")
	public String postLogin(@RequestParam("username") String email, @RequestParam("password") String password,
			Model model, HttpSession session, HttpServletRequest request) {
		// 성공과 체크를 ... 확인할 방법이 없나.....???/
//		System.out.println("postLogin ... : " + request.getAttribute("status"));
		User preU = (User) session.getAttribute(MemberConstants.SESSION_USER);
		User u = null;
		try {
			u = memberService.login(email, password);
			if (u == null || "FAIL".equals(request.getAttribute("status"))) {
				throw new Exception("Incorrect email or password.");
			}
		} catch (Exception e) {
			logger.error(e.getMessage() + " email = " + email + " user : " + u);
			e.printStackTrace();
			model.addAttribute("error", e.getMessage());
			model.addAttribute("email", email);
			return "/member/login";
		}

		if (preU != null) {
			session.setAttribute(MemberConstants.SESSION_USER, null);
			session.invalidate();
		}
		// 로그인 후 처리 로직
		// 내부,외부 여부
		// 사용자 정보 쿠키저장
		// 사용자 토큰 생성
		// 암호 만료 여부 확인
		// 중복 로그인 관련 설정
		// 권한 설정
		// 세션 정보 저장

		// 로그인 후 처리 로직
		session.setAttribute(MemberConstants.SESSION_USER, u);
		return "redirect:/";
	}

	@GetMapping("/join")
	public String join() {
		return "/member/join";
	}

	@PostMapping("/join")
	public String postJoin(@RequestParam("username") String name, @RequestParam("password") String password,
			@RequestParam("email") String email, Model model) {

		User u = new User();
		u.setName(name);
		u.setEmail(email);
		u.setPassword(password);
		if (memberService.findUserByEmail(email) != null) {
			model.addAttribute("error", "There were problems creating your account.");
			// 에러페이지로 이동
			return "/member/join";
		}
		;
		logger.error("user data : " + u);
		u = memberService.saveUser(u);
		logger.error("user save data : " + u);
		return "redirect:/";
	}

	@GetMapping("/password_reset")
	public String passwordReset() {
		return "/member/passwordReset";
	}

	@PostMapping("/password_reset")
	public String postPasswordReset(@RequestParam("email") String email, Model model) {
		User u = memberService.findUserByEmail(email);
		if (u == null) {
			model.addAttribute("error", "Can't find that email, sorry.");
			return "/member/passwordReset";
		} else {
			String tempPassword = UUID.randomUUID().toString().replaceAll("-", "").substring(16);
			u.setPassword(tempPassword);
			memberService.updateUserByEmail(u);
			emailSender.emailSender(email, tempPassword);
			// Async로 처리할 방법을 찾아야 한다...
//			SendMailThread smtpThread = new SendMailThread(email,tempPassword);
//			smtpThread.run();
			return "redirect:/member/login";
		}
	}

	@GetMapping("/logout")
	public String logout(HttpSession session) {
		return "redirect:/";
	}
}
