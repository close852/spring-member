package com.cjhm.oauth2.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.CompositeFilter;

import static com.cjhm.member.enums.SocialType.FACEBOOK;
import static com.cjhm.member.enums.SocialType.GOOGLE;
import static com.cjhm.member.enums.SocialType.KAKAO;

import com.cjhm.member.enums.SocialType;
import com.cjhm.oauth2.ClientResources;
import com.cjhm.oauth2.entity.service.UserService;
import com.cjhm.oauth2.service.UserTokenService;

@Configuration
@EnableWebSecurity
@EnableOAuth2Client
public class OAuthConfig extends WebSecurityConfigurerAdapter{

	//Spring Security로 userDetails 작성하여 인증하기 위해서 필수
	//... auth 처리 방법 알아내기
	@Autowired
	UserService userService;
	
	//UserDetailService에서 password암호화 해야됨
	@Autowired
	ShaPasswordEncoder passwordEncoder;
	
	@Autowired
	private OAuth2ClientContext oAuth2ClientContext;
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		CharacterEncodingFilter filter = new CharacterEncodingFilter("UTF-8");
		http.authorizeRequests()
				.antMatchers("/"
							,"/apis/**"
							,"/member/login","/member/join","/member/logout","/member/password_reset"
							,"/css/**","/img/**","/js/**","/webjars/**"
//							,"/member/login/**"
							,"/sample/**")
				.permitAll()
//				.antMatchers("/facebook").hasAnyAuthority(FACEBOOK.getRoleType())
//				.antMatchers("/google").hasAnyAuthority(GOOGLE.getRoleType())
//				.antMatchers("/kakao").hasAnyAuthority(KAKAO.getRoleType())
				.anyRequest().authenticated()
			.and()
				//XFrameOptionsHeaderWriter의 최적설정 사용 안함.
				.headers().frameOptions().disable()				
			.and()
				.formLogin()
				.loginPage("/member/login")
//				.usernameParameter("username")
//				.passwordParameter("password")
				.successForwardUrl("/member/login") //성공 경로 post /member/login
				.failureHandler((request, response, exception) -> {
					System.out.println("??");
					request.setAttribute("status", "FAIL");
					RequestDispatcher dispatcher = request.getRequestDispatcher("/member/login");
					dispatcher.forward(request, response);
				})
			.and()
				.exceptionHandling()
				//예외접근에 대한 처리
				.authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/"))
				.accessDeniedHandler((req,res,e)->{
					System.out.println(e.getMessage());
				})
			.and()
				.logout()
				.logoutUrl("/member/logout")
				.logoutSuccessUrl("/")
				.deleteCookies("JSESSIONID")
				.invalidateHttpSession(true)
			.and()
				//와 미친.... csrf 안하면 POST로 호출하는거 다 Exception 처리됨....
				.addFilterBefore(filter, CsrfFilter.class)
				.addFilterBefore(oauth2Filter(), BasicAuthenticationFilter.class)
				.csrf().disable()
				;
	}
	
	@Bean
	public FilterRegistrationBean<OAuth2ClientContextFilter> oauth2ClientFilterRegistration(OAuth2ClientContextFilter filter) {
		System.err.println("oauth2ClientFilterRegistration");
		FilterRegistrationBean<OAuth2ClientContextFilter> registration = new FilterRegistrationBean<>();
		registration.setFilter(filter);
		registration.setOrder(-100);
		return registration;
		
	}
	
	private Filter oauth2Filter() {
		System.err.println("oauth2Filter 이건 된...");
		CompositeFilter filter = new CompositeFilter();
		List<Filter> filters = new ArrayList<>();
		filters.add(oauth2Filter(facebook(),"/member/login/facebook", FACEBOOK));
		filters.add(oauth2Filter(kakao(), 	"/member/login/kakao", KAKAO));
		filters.add(oauth2Filter(google(), 	"/member/login/google", GOOGLE));
		return filter;
	}
	private Filter oauth2Filter(ClientResources client, String path, SocialType socialType) {
		System.err.println("oauth2Filter.... param3개"+socialType);
		OAuth2ClientAuthenticationProcessingFilter filter = new OAuth2ClientAuthenticationProcessingFilter(path);
		OAuth2RestTemplate template = new OAuth2RestTemplate(client.getClient(),oAuth2ClientContext);
		filter.setRestTemplate(template);
		filter.setTokenServices(new UserTokenService(client,socialType));
		filter.setAuthenticationSuccessHandler((req,res,auth)->res.sendRedirect("/member/"+socialType.getValue()+"/complate"));
		filter.setAuthenticationFailureHandler((req,res,e)->res.sendRedirect("/error"));
		
		return filter;
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