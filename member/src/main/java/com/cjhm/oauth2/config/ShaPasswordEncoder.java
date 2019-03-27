package com.cjhm.oauth2.config;

import java.security.MessageDigest;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class ShaPasswordEncoder implements PasswordEncoder {

	@Override
	public String encode(CharSequence rawPassword) {
		StringBuffer hexString = new StringBuffer();
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(rawPassword.toString().getBytes("UTF-8"));
			for (int i = 0; i < hash.length; i++) {
				String hex = Integer.toHexString(0xff & hash[i]);
				if (hex.length() == 1)
					hexString.append('0');
				hexString.append(hex);
			}
//			 출력C
			System.out.println(hexString.toString());
		} catch (

		Exception ex) {
			throw new RuntimeException(ex);
		}
		return hexString.toString();
//		return rawPassword.toString();
	}

	@Override
	public boolean matches(CharSequence rawPassword, String encodedPassword) {
		System.out.println("matches : " + rawPassword + "/" + encodedPassword);
//		return encode(rawPassword).equals(encodedPassword);
		return (rawPassword).equals(encodedPassword);
	}

}
