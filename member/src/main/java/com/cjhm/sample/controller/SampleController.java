package com.cjhm.sample.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/sample")
public class SampleController {
	
	@GetMapping("/join")
	public String join() {
		return "/sample/join";
	}

	@GetMapping("/login")
	public String login() {
		return "/sample/login";
	}
}
