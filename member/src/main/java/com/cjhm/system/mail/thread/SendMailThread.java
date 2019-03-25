package com.cjhm.system.mail.thread;

import org.springframework.beans.factory.annotation.Autowired;

import com.cjhm.system.mail.service.EmailSender;

public class SendMailThread implements Runnable {

	private String email;
	private String tempPassword;

	@Autowired
	EmailSender emailSender;

	public SendMailThread(String email, String tempPassword) {
		this.email = email;
		this.tempPassword = tempPassword;
	}

	@Override
	public void run() {
		System.out.println("SMTP 메일 발송 시작 : "+email);
		emailSender.emailSender(email,tempPassword);
		System.out.println("SMTP 메일 발송 종료 : "+email);
	}

}
