package com.cjhm.oauth2;

import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;

public class ClientResources {

	@NestedConfigurationProperty
	private AuthorizationCodeResourceDetails client = new AuthorizationCodeResourceDetails();
	
	@NestedConfigurationProperty
	private OAuth2ResourceServerProperties resource = new OAuth2ResourceServerProperties();
	
	public AuthorizationCodeResourceDetails getClient() {
		return client;
	}
	
	public OAuth2ResourceServerProperties getResource() {
		return resource;
	}
}
