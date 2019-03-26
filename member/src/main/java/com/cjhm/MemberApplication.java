package com.cjhm;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.cjhm.member.repository.MemberRepository;

@SpringBootApplication
@Controller
public class MemberApplication {

	@Autowired
	MemberRepository memberRepository;

	@GetMapping("/")
	public String index(Model m, HttpSession session) {
//		User u = (User) session.getAttribute(MemberConstants.SESSION_USER);
//		if(u!=null) {
//			return "/index";
//		}
		// 세션여부 확인
		return "/index";
	}

	public static void main(String[] args) {
		SpringApplication.run(MemberApplication.class, args);
	}

//	@Bean
//	public CommandLineRunner init() {
//		System.out.println("init!!");
//		return (args) -> {
//			User user = new User();
//			user.setEmail("close852@naver.com");
//			user.setPassword("1234");
//			user.setName("jiwoo");
//			memberRepository.save(user);
//		};
//	}

}
