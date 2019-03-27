package com.cjhm.oauth2.entity.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;

public class SecurityUtils {

	private static Collection<? extends GrantedAuthority> authorities(com.cjhm.member.entity.User u) {
		List<GrantedAuthority> authorities = new ArrayList<>();
		if (!StringUtils.isEmpty(u.getSocialType())) {
			authorities.add(new SimpleGrantedAuthority(u.getSocialType().getRoleType()));
		} else {
			authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
		}
		return authorities;
	}

	public static UserDetails makeSecurityUserData(com.cjhm.member.entity.User u) {
//		ShaPasswordEncoder e = new ShaPasswordEncoder();
//		u.setPassword(e.encode(u.getPassword()));
		return new User(u.getEmail(), u.getPassword(), authorities(u));
	}

}
