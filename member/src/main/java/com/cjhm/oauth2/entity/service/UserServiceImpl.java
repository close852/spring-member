package com.cjhm.oauth2.entity.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.cjhm.member.entity.User;
import com.cjhm.member.repository.MemberRepository;
import com.cjhm.oauth2.entity.utils.SecurityUtils;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	MemberRepository memberRepository;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		System.err.println("loadUserByUsername : " + email);
		User u = memberRepository.findByEmail(email);
		System.out.println(u);
		if (u == null) {
			throw new UsernameNotFoundException(email);
		}
		return SecurityUtils.makeSecurityUserData(u);
	}

}
