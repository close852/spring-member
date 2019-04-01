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

		User u = memberRepository.findByEmail(email);
		logger.info("user info : " + u);
		return u;
	}

	public User findUserByEmail(String email) {
		logger.info("login info : " + email);
		User u = memberRepository.findByEmail(email);
		logger.info("user info : " + u);

		return u;

	}
	public User findAuthUserByEmail(String email,String principal) {
		logger.info("login info : " + email);
		User u = memberRepository.findByEmailAndPrincipal(email,principal);
		logger.info("user info : " + u);
		
		return u;
		
	}
	
	public User updateUserByEmail(User u) {
		return memberRepository.save(u);
	}

	public User saveUser(User u) {
		
		return memberRepository.save(u);
	}

//	public UserDetails loadUserByUsername(String email) {
//		User user = memberRepository.findByEmail(email);
//		GrantedAuthority authority;
//		UserDetails users = new User();
//		return new UserDetailsServiceImpl(new UserDetails(u));
//	}

}
