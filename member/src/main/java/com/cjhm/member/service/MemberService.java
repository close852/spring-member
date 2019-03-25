package com.cjhm.member.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.cjhm.member.entity.User;
import com.cjhm.member.repository.MemberRepository;

@Service
public class MemberService {

	MemberRepository memberRepository;

	Logger logger = LoggerFactory.getLogger(MemberService.class);

	public MemberService(MemberRepository memberRepository) {
		this.memberRepository = memberRepository;
	}

	public User login(String email, String password) {

		logger.info("login info : " + email + " : " + password);
		User u = memberRepository.findByEmail(email);
		logger.info("user info : " + u);
		String encriptPassword = encript(password);
		if (u != null && u.getPassword().equals(encriptPassword)) {
			return u;
		}
		return null;
	}

	public User findUserByEmail(String email) {
		logger.info("login info : " + email);
		User u = memberRepository.findByEmail(email);
		logger.info("user info : " + u);

		return u;

	}
	
	public User updateUserByEmail(User u) {
		return memberRepository.save(u);
	}

	private String encript(String password) {
		return password;
	}

	public User saveUser(User u) {
		
		return memberRepository.save(u);
	}

}
