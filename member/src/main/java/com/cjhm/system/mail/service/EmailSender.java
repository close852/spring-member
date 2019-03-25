package com.cjhm.system.mail.service;

import java.util.List;

public interface EmailSender {

	 public <T> boolean emailSender(T report);
	 public <T> boolean emailSender(String to,T report);
	 public <T> List<String> emailSender(List<String> toList,T report);
}
