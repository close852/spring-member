package com.cjhm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cjhm.member.entity.User;
import com.cjhm.member.repository.MemberRepository;

@SpringBootApplication
@RestController
public class MemberApplication {

	@Autowired
	MemberRepository memberRepository;
	@GetMapping("/")
	public String index() {
		return "index";
	}

	public static void main(String[] args) {
		SpringApplication.run(MemberApplication.class, args);
	}
	
	@Bean
	public CommandLineRunner init() {
		return (args)->{
			User user = new User();
			user.setEmail("1234");
			user.setPassword("1234");
			user.setName("jiwoo");
			memberRepository.save(user);
		};
	}

}
