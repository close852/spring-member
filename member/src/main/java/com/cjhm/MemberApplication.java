package com.cjhm;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.cjhm.member.repository.MemberRepository;
import com.cjhm.resolver.UserArgumentResolver;

@SpringBootApplication
@Controller
public class MemberApplication implements WebMvcConfigurer{

	private Logger logger = LoggerFactory.getLogger(MemberApplication.class);

	@Autowired
	MemberRepository memberRepository;

	@Autowired
	private UserArgumentResolver userArgumentResolver;
	
	@GetMapping("/")
	public String index() {
		return "/index";
	}

	public static void main(String[] args) { 
		SpringApplication.run(MemberApplication.class, args);
	}
	
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
		logger.info("userArgumentResolver added");
		argumentResolvers.add(userArgumentResolver);
	}

	

}
