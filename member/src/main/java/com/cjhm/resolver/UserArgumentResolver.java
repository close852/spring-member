package com.cjhm.resolver;

import static com.cjhm.member.enums.SocialType.FACEBOOK;
import static com.cjhm.member.enums.SocialType.GOOGLE;
import static com.cjhm.member.enums.SocialType.KAKAO;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.core.MethodParameter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.cjhm.annotation.SocialUser;
import com.cjhm.member.constants.MemberConstants;
import com.cjhm.member.entity.User;
import com.cjhm.member.enums.SocialType;
import com.cjhm.member.repository.MemberRepository;

@Component
public class UserArgumentResolver implements HandlerMethodArgumentResolver {

	private MemberRepository memberRepository;

	public UserArgumentResolver(MemberRepository memberRepository) {
		System.out.println("resolver!");
		this.memberRepository = memberRepository;
	}

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return parameter.getParameterAnnotation(SocialUser.class) != null
				&& parameter.getParameterType().equals(User.class);
	}

	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
		System.out.println("세션생성!");
		HttpSession session = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest().getSession();
		User user = (User) session.getAttribute(MemberConstants.SESSION_USER);
		System.out.println(user);
		return getUser(user, session); 
	}

	private User getUser(User user, HttpSession session) {
		if (user == null) {
			try {
				//ROLE_USER....
				OAuth2AuthenticationToken authentication = (OAuth2AuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
				Map<String,Object> map = authentication.getPrincipal().getAttributes();
				System.out.println("getUser Map info...START");
				for(String o : map.keySet()) {
					System.out.println(o+" : "+map.get(o));
				}
				System.out.println("getUser Map info...END");
				System.out.println("authentication.getAuthorizedClientRegistrationId() ::"+authentication.getAuthorizedClientRegistrationId());
				User convertUser = convertUser(authentication.getAuthorizedClientRegistrationId(), map);
				System.out.println("convertUser :: "+convertUser);
				user = memberRepository.findByEmail(convertUser.getEmail());
				System.out.println("user :: "+user);
				if (user == null) {
					user = memberRepository.save(convertUser);
				}
				else {
					user.setSocialType(convertUser.getSocialType());
				}
				setRoleIfNotSame(user, authentication, map);
				session.setAttribute(MemberConstants.SESSION_USER, user);
			} catch (ClassCastException e) {
				return user;
			}
		} 
		return user;
	}

	private User convertUser(String authority, Map<String, Object> map) {
		System.out.println("authority ::"+ authority + " / "+FACEBOOK.getValue());
		if (FACEBOOK.getValue().equals(authority)) {
			System.out.println("facebook 들어오고...");
			return getModernUser(FACEBOOK, map);
		} else if (GOOGLE.getValue().equals(authority)) {
			return getModernUser(GOOGLE, map);
		} else if (KAKAO.getValue().equals(authority)) {
			return getKakaoUser(map);
		}
		return null;
	}

	private User getKakaoUser(Map<String, Object> map) {
		HashMap<String, Object> propertiesMap = (HashMap<String, Object>)(Object)map.get("properties");
		User user = new User();
		user.setName(String.valueOf(propertiesMap.get("name")));
		user.setEmail(String.valueOf(propertiesMap.get("email")));
		user.setPrincipal(String.valueOf(propertiesMap.get("id")));
		user.setSocialType(KAKAO);
		user.setCreateDate();

		return user;
	}
	private User getModernUser(SocialType socialType, Map<String, Object> map) {
		System.out.println(socialType);
		User user = new User();
		user.setName(String.valueOf(map.get("name")));
		user.setEmail(String.valueOf(map.get("email")));
		user.setPrincipal(String.valueOf(map.get("id")));
		user.setSocialType(socialType);
		user.setCreateDate();
		return user;
	}
	
	private void setRoleIfNotSame(User user, OAuth2AuthenticationToken authentication,Map<String, Object> map) {
		System.out.println("setRoleIfNotSame ! "+ authentication.getAuthorities());
		if(!authentication.getAuthorities().contains(new SimpleGrantedAuthority(user.getSocialType().getRoleType()))) {
			SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(map,"N/A",
					AuthorityUtils.createAuthorityList(user.getSocialType().getRoleType())));
		}
	}

}
