package com.cjhm.system.mail.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class MailContentBuilder {

	@Autowired
	private TemplateEngine templateEngine;
	
	public String build(String email,String authNumber) {
		Context context = new Context();
		context.setVariable("title", "퍼즐몰");
		context.setVariable("email", email);
		context.setVariable("authNumber", authNumber);
		return templateEngine.process("/system/mail/mailContentTemplate", context);
	}
}
