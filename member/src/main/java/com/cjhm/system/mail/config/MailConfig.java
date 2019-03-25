package com.cjhm.system.mail.config;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class MailConfig {

	private Logger log = LoggerFactory.getLogger(MailConfig.class);
	
	@Value("${spring.mail.host}")
	private String MAIL_HOST;
	@Value("${spring.mail.port}")
	private int MAIL_PORT;
	@Value("${spring.mail.username}")
	private String MAIL_USERNAME;
	@Value("${spring.mail.password}")
	private String MAIL_PASSWORD;
	private String DEFAULT_ENCODE="UTF-8";
	@Value("${spring.mail.smtp.start-tls-enable}")
	private boolean SMTP_TLS;
	@Value("${spring.mail.smtp.auth}")
	private boolean SMTP_AUTH;
	
	@Bean
	public JavaMailSender mailSender() {
		JavaMailSenderImpl senderImpl = new JavaMailSenderImpl();
		senderImpl.setHost(MAIL_HOST);
		senderImpl.setPort(MAIL_PORT);
		senderImpl.setUsername(MAIL_USERNAME);
		senderImpl.setPassword(MAIL_PASSWORD);
		senderImpl.setDefaultEncoding(DEFAULT_ENCODE);
		
		Properties mailProps = new Properties();
		mailProps.put("mail.smtp.auth", SMTP_AUTH);
		mailProps.put("mail.smtp.starttls.enable", SMTP_TLS);
		log.error(String.format("mail info : host=%s, port=%d, username=%s,password=%s,tls=%s,auth=%s", MAIL_HOST,MAIL_PORT,MAIL_USERNAME,MAIL_PASSWORD,SMTP_TLS,SMTP_AUTH));
		senderImpl.setJavaMailProperties(mailProps);
		return senderImpl;
	}
}
