package com.cjhm.system.mail.service;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.mail.internet.MimeUtility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.cjhm.system.mail.utils.MailContentBuilder;

@Service
public class EmailSenderImpl implements EmailSender {

	private static final Logger log = LoggerFactory.getLogger(EmailSenderImpl.class);
	@Autowired
	JavaMailSender javaMailSender;

	@Value("${spring.mail.username}")
	private String MAIL_USERNAME;

	@Autowired
	MailContentBuilder mailContentBuilder;
	
	@Override
	public <T> boolean emailSender(T report) {
		log.debug("Send report By mail...");
		boolean refVal = false;
		final String emailFrom = "퍼즐몰<"+MAIL_USERNAME+">";
		final String emailTo = "close852@naver.com";
		final String subject = "test subject";
		final String message = mailContentBuilder.build(emailTo,(String)report);

		try {

			javaMailSender.send((mimeMessage) -> {
				MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

				helper.setTo(emailTo);
				helper.setFrom(emailFrom);
				helper.setSubject(subject);
				//HTML 여부
				helper.setText(message,true);

//				final File file = new File("temp filename");
//				helper.addAttachment(MimeUtility.encodeText(file.getName()), () -> {
//					return new FileInputStream(file);
//				});
			});
			return refVal;
		} catch (Exception e) {
			log.error("메일 발송 실패! To :" + emailTo + ",From : " + emailFrom + ", subject :" + subject);
			log.error("message : " + message);
			log.error(e.getMessage(), e);
		}
		return refVal;
	}

	@Override
	public <T> boolean emailSender(String to, T report) {
		log.debug("Send report By mail...");
		boolean refVal = false;
		final String emailFrom = "퍼즐몰<"+MAIL_USERNAME+">";
		final String emailTo = to;
		final String subject = "[퍼즐몰] 인증번호 발송 안내 메일 입니다.";
		final String message = mailContentBuilder.build(emailTo,(String)report);

		try {
			javaMailSender.send((mimeMessage) -> {
				MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

				helper.setTo(emailTo);
				helper.setFrom(emailFrom);
				helper.setSubject(subject);
				helper.setText(message,true);

			});
			return refVal;
		} catch (Exception e) {
			log.error("메일 발송 실패! To :" + emailTo + ",From : " + emailFrom + ", subject :" + subject);
			log.error("message : " + message);
			log.error(e.getMessage(), e);
		}
		return refVal;
	}

	@Override
	public <T> List<String> emailSender(List<String> toList, T report) {
		boolean refVal = false;
		List<String> sendFailRcvList = new ArrayList<String>();
		for(String rcvId : toList) {
			refVal =emailSender(rcvId,report);
			if(!refVal) {
				sendFailRcvList.add(rcvId);
			}
		}
		return sendFailRcvList;
	}

}
