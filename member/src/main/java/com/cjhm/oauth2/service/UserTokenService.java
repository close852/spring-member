package com.cjhm.oauth2.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.boot.autoconfigure.security.oauth2.resource.AuthoritiesExtractor;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoTokenServices;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;

import com.cjhm.member.enums.SocialType;
import com.cjhm.oauth2.ClientResources;

/*
 * UserInfoTokenServices 사용을 위해서 spring-security-oauth2-autoconfigure 추가
 * 
 */
public class UserTokenService extends UserInfoTokenServices {
	public UserTokenService(ClientResources resources, SocialType socialType) {
		super(resources.getResource().getTokenInfoUri(), resources.getClient().getClientId());
		Map<String, String> map = new HashMap<>();
		setAuthoritiesExtractor(new OAuth2AuthoritiesExtractor(socialType));
	}

	public static class OAuth2AuthoritiesExtractor implements AuthoritiesExtractor {

		private String socialType;

		public OAuth2AuthoritiesExtractor(SocialType socialType) {
			System.out.println("OAuth2AuthoritiesExtractor 생성자");
			this.socialType = socialType.getRoleType();
		}

		@Override
		public List<GrantedAuthority> extractAuthorities(Map<String, Object> map) {
			System.out.println("extractAuthorities 메소드 호출");
			return AuthorityUtils.createAuthorityList(this.socialType);
		}

	}
}
