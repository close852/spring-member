package com.cjhm.oauth2.config;

import static com.cjhm.member.enums.SocialType.FACEBOOK;
import static com.cjhm.member.enums.SocialType.GOOGLE;
import static com.cjhm.member.enums.SocialType.KAKAO;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.RequestDispatcher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.CompositeFilter;

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
							,"/sample/**")
				.permitAll()
				.antMatchers("/facebook").hasAnyAuthority(FACEBOOK.getRoleType())
				.antMatchers("/google").hasAnyAuthority(GOOGLE.getRoleType())
				.antMatchers("/kakao").hasAnyAuthority(KAKAO.getRoleType())
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
	public FilterRegistrationBean<OAuth2ClientContextFilter> oauth2ClientFilterRegistration(OAuth2ClientContextFilter filter){
		FilterRegistrationBean<OAuth2ClientContextFilter> registration = new FilterRegistrationBean<>();
		registration.setFilter(filter);
		registration.setOrder(-100);
		return registration;
	}

	
	private Filter oauth2Filter() {
		CompositeFilter filter = new CompositeFilter(); 
		List<Filter> filters = new ArrayList<>();
		filters.add(oauth2Filter(facebook(),"/member/login/facebook", FACEBOOK));
		filters.add(oauth2Filter(google(), 	"/member/login/google", GOOGLE));
//		filters.add(oauth2Filter(kakao(), 	"/member/login/kakao", KAKAO));
		filter.setFilters(filters);
		return filter;
	}
	private Filter oauth2Filter(ClientResources client, String path, SocialType socialType) {
		OAuth2ClientAuthenticationProcessingFilter filter = new OAuth2ClientAuthenticationProcessingFilter(path);
		OAuth2RestTemplate template = new OAuth2RestTemplate(client.getClient(),oAuth2ClientContext);
		filter.setRestTemplate(template); 
		filter.setTokenServices(new UserTokenService(client,socialType));
		filter.setAuthenticationSuccessHandler((req,res,auth)->{
			System.out.println("Succeess");
			res.sendRedirect("/member/"+socialType.getValue()+"/complete");
		});
		filter.setAuthenticationFailureHandler((req,res,e)->{
			System.out.println("FAIL!");
			System.out.println(e.getMessage());
			res.sendRedirect("/error");
		});
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