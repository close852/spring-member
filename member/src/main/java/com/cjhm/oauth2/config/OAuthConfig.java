package com.cjhm.oauth2.config;

import static com.cjhm.member.enums.SocialType.FACEBOOK;
import static com.cjhm.member.enums.SocialType.GOOGLE;
import static com.cjhm.member.enums.SocialType.KAKAO;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.servlet.RequestDispatcher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.web.filter.CharacterEncodingFilter;

import com.cjhm.oauth2.entity.service.UserService;
import com.cjhm.oauth2.enums.CustomOAuth2Provider;

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
		CharacterEncodingFilter filter = new CharacterEncodingFilter("UTF-8");
		http.authorizeRequests()
				.antMatchers("/"
							,"/apis/**"
							,"/member/login","/member/join","/member/logout","/member/password_reset"
//							,"/login/member/**"
							,"/css/**","/img/**","/js/**","/webjars/**"
							,"/sample/**")
				.permitAll()
				.antMatchers("/facebook").hasAnyAuthority(FACEBOOK.getRoleType())
				.antMatchers("/google").hasAnyAuthority(GOOGLE	.getRoleType())
				.antMatchers("/kakao").hasAnyAuthority(KAKAO.getRoleType())
				.anyRequest().authenticated()
			.and()
				//XFrameOptionsHeaderWriter의 최적설정 사용 안함.
				.headers().frameOptions().disable()				
			.and()
				.oauth2Login()
				.defaultSuccessUrl("/loginSuccess")
				.failureHandler((request, response, exception) -> {
					System.out.println("FAIL");
//					RequestDispatcher dispatcher = request.getRequestDispatcher("/loginFailure");
//					dispatcher.forward(request, response);
					response.sendRedirect("/loginFailure");
				})
			.and()
				.formLogin()
				.loginPage("/member/login")
//				.usernameParameter("username")
//				.passwordParameter("password")
				.successForwardUrl("/member/login") //성공 경로 post /member/login
				.failureHandler((request, response, exception) -> {
					System.out.println("login fail" +exception.getMessage());
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
				.csrf().disable()
				;
	}

	@Bean
	public ClientRegistrationRepository clientRegistrationRepository(OAuth2ClientProperties propoerties , @Value("${custom.oauth2.kakao.client-id}") String kakaoClientId){
//		List<ClientRegistration> registrations = propoerties.getRegistration().keySet().stream()
//					.map(client ->  getRegistration(propoerties,client)).filter(Objects::nonNull).collect(Collectors.toList()) ;
		System.out.println("clientRegistrationRepository : " + kakaoClientId);
		System.out.println(propoerties);
		List<ClientRegistration> registrations  = propoerties.getRegistration().keySet().stream()
						.map(client ->  getRegistration(propoerties,client))
						.filter(Objects::nonNull)
						.collect(Collectors.toList());
		registrations.add(CustomOAuth2Provider.KAKAO.getBuilder("kakao")
						.clientId(kakaoClientId)
						.clientSecret("x")
						.jwkSetUri("x").build());
		return new InMemoryClientRegistrationRepository(registrations);
	}
	private ClientRegistration getRegistration(OAuth2ClientProperties properties, String client) {
		
		if("google".equals(client)) {
			OAuth2ClientProperties.Registration registration = properties.getRegistration().get("google");
			return CommonOAuth2Provider.GOOGLE.getBuilder(client)
					.clientId(registration.getClientId())
					.clientSecret(registration.getClientSecret())
					.scope("email","profile")
					.build();
		}
		if("facebook".equals(client)) {
			OAuth2ClientProperties.Registration registration = properties.getRegistration().get("facebook");
			return CommonOAuth2Provider.FACEBOOK.getBuilder(client)
					.clientId(registration.getClientId())
					.clientSecret(registration.getClientSecret())
					.userInfoUri("https://graph.facebook.com/me?fields=id,name,email,link")
					.scope("email")
					.build();
		}
		// TODO Auto-generated method stub
		return null;
	}

	@Autowired
	protected void glbalConfigure(AuthenticationManagerBuilder auth) throws Exception  {
		auth.userDetailsService(userService).passwordEncoder(passwordEncoder);
	}
	
}