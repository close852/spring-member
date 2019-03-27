package com.cjhm.oauth2.config;

import javax.servlet.RequestDispatcher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

import com.cjhm.oauth2.ClientResources;
import com.cjhm.oauth2.entity.service.UserService;

@Configuration
@EnableWebSecurity
public class OAuthConfig extends WebSecurityConfigurerAdapter{

	//Spring Security로 userDetails 작성하여 인증하기 위해서 필수
	//... auth 처리 방법 알아내기
	@Autowired
	UserService userService;
	
	//UserDetailService에서 password암호화 해야됨
	@Autowired
	ShaPasswordEncoder passwordEncoder;
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
				.antMatchers("/"
							,"/apis/**"
							,"/member/login","/member/join","/member/logout","/member/password_reset"
							,"/css/**","/img/**","/js/**","/webjars/**")
				.permitAll()
				.anyRequest().authenticated()
			.and()
				.formLogin()
				.loginPage("/member/login")
//				.usernameParameter("username")
//				.passwordParameter("password")
				.successForwardUrl("/member/login") //성공 경로 post /member/login
				.failureHandler((request, response, exception) -> {
					request.setAttribute("status", "FAIL");
					RequestDispatcher dispatcher = request.getRequestDispatcher("/member/login");
					dispatcher.forward(request, response);
				})
			.and()
				.exceptionHandling()
				//예외접근에 대한 처리
				.authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/"))
			.and()
				.headers().frameOptions().disable()
			.and()
				.logout()
				.logoutUrl("/member/logout")
				.logoutSuccessUrl("/")
				.deleteCookies("JSESSIONID")
				.invalidateHttpSession(true)
			.and()
				//와 미친.... csrf 안하면 POST로 호출하는거 다 Exception 처리됨....
				.csrf().disable()
				;
	}

	@Bean
	@ConfigurationProperties("facebook")
	public ClientResources facebook() {
		return new ClientResources();
	}

	@Bean
	@ConfigurationProperties("google")
	public ClientResources google() {
		return new ClientResources();
	}

	@Bean
	@ConfigurationProperties("kakao")
	public ClientResources kakao() {
		return new ClientResources(); 
	}

	@Autowired
	protected void glbalConfigure(AuthenticationManagerBuilder auth) throws Exception  {
		auth.userDetailsService(userService).passwordEncoder(passwordEncoder);
	}
	
}