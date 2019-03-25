package com.cjhm.member;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.cjhm.system.mail.service.EmailSender;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MemberApplicationTests {

	@Autowired EmailSender emailSender;
	@Test
	public void contextLoads() {
		emailSender.emailSender("발송...!!");
	}
}
