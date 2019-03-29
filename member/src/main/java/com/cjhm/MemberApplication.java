package com.cjhm;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.cjhm.member.repository.MemberRepository;
import com.cjhm.resolver.UserArgumentResolver;

@SpringBootApplication
@Controller
public class MemberApplication implements WebMvcConfigurer{

	@Autowired
	MemberRepository memberRepository;

	@Autowired
	private UserArgumentResolver userArgumentResolver;
	
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
	
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
		argumentResolvers.add(userArgumentResolver);
	}

	

}
