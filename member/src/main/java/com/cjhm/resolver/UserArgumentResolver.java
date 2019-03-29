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
		HttpSession session = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest().getSession();
		User user = (User) session.getAttribute(MemberConstants.SESSION_USER);
		return getUser(user, session);
	}

	private User getUser(User user, HttpSession session) {
		if (user == null) {
			try {
				OAuth2Authentication authentication = (OAuth2Authentication) SecurityContextHolder.getContext().getAuthentication();
				Map<String, Object> map = (HashMap<String, Object>) authentication.getUserAuthentication().getDetails();
				System.out.println("getUser Map info...START");
				for(String o : map.keySet()) {
					System.out.println(o+" : "+map.get(o));
				}
				System.out.println("getUser Map info...END");
				User convertUser = convertUser(String.valueOf(authentication.getAuthorities().toArray()[0]), map);
				user = memberRepository.findByEmail(convertUser.getEmail());
				if (user == null) {
					user = memberRepository.save(convertUser);
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
		if (FACEBOOK.getRoleType().equals(authority)) {
			return getModernUser(FACEBOOK, map);
		} else if (GOOGLE.getRoleType().equals(authority)) {
			return getModernUser(GOOGLE, map);
		} else if (KAKAO.getRoleType().equals(authority)) {
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
		User user = new User();
		user.setName(String.valueOf(map.get("name")));
		user.setEmail(String.valueOf(map.get("email")));
		user.setPrincipal(String.valueOf(map.get("id")));
		user.setSocialType(socialType);
		user.setCreateDate();
		return user;
	}
	
	private void setRoleIfNotSame(User user, OAuth2Authentication authentication,Map<String, Object> map) {
		if(!authentication.getAuthorities().contains(new SimpleGrantedAuthority(user.getSocialType().getRoleType()))) {
			SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(map,"N/A",
					AuthorityUtils.createAuthorityList(user.getSocialType().getRoleType())));
		}
	}

}
