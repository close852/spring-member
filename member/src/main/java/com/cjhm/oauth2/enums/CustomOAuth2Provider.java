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
			ClientRegistration.Builder builder = getBuilder(registrationId,ClientAuthenticationMethod.POST,DEFAULT_LOGIN_REDIRECT_URL);
			builder.scope("profile");
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
	};
	
	private static final String DEFAULT_LOGIN_REDIRECT_URL="{baseUrl}/loginSuccess";

	public abstract ClientRegistration.Builder getBuilder(String registrationId);
}
