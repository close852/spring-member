package com.cjhm.oauth2.enums;

import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistration.Builder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;

public enum CustomOAuth2Provider {

	KAKAO {
		@Override
		public ClientRegistration.Builder getBuilder(String registrationId) {
			System.out.println("ㅕ기...?");
			ClientRegistration.Builder builder = getBuilder(registrationId,ClientAuthenticationMethod.POST,"{baseUrl}/loginSuccess/kakao");
			builder.scope("profile","account_email");
			builder.authorizationUri("https://kauth.kakao.com/oauth/authorize");
			builder.tokenUri("https://kauth.kakao.com/oauth/token");
			builder.userInfoUri("https://kapi.kakao.com/v1/user/me");
			builder.userNameAttributeName("id");
			builder.clientName("kakao");
			return builder;
		}
		private Builder getBuilder(String registrationId, ClientAuthenticationMethod method, String redirectUri) {
			ClientRegistration.Builder builder = ClientRegistration.withRegistrationId(registrationId);
			builder.clientAuthenticationMethod(method);
			builder.authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE);
			builder.redirectUriTemplate(redirectUri);
			return builder;
		}	
	},
	NAVER {
		@Override
		public ClientRegistration.Builder getBuilder(String registrationId) {
			System.out.println("ㅕ기...?");
//			ClientRegistration.Builder builder = getBuilder(registrationId,ClientAuthenticationMethod.BASIC,"http://127.0.0.1:8080/oauth2/naver/complete");
			ClientRegistration.Builder builder = getBuilder(registrationId,ClientAuthenticationMethod.BASIC,"{baseUrl}/loginSuccess/naver");
			builder.scope("profile");
			builder.authorizationUri("https://nid.naver.com/oauth2.0/authorize");
			builder.tokenUri("https://nid.naver.com/oauth2.0/token");
			builder.userInfoUri("https://kapi.kakao.com/v1/user/me");
			builder.userNameAttributeName("id");
			builder.clientName("naver");
			return builder;
		}
		
		private Builder getBuilder(String registrationId, ClientAuthenticationMethod method, String redirectUri) {
			ClientRegistration.Builder builder = ClientRegistration.withRegistrationId(registrationId);
			builder.clientAuthenticationMethod(method);
			builder.authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE);
			builder.redirectUriTemplate(redirectUri);
			return builder;
		}	
	};
	
	public abstract ClientRegistration.Builder getBuilder(String registrationId);
}
