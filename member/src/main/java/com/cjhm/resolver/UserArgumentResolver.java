package com.cjhm.resolver;

import static com.cjhm.member.enums.SocialType.FACEBOOK;
import static com.cjhm.member.enums.SocialType.GOOGLE;

import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.core.MethodParameter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
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
		System.out.println("여기옴??");
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
		System.out.println("사용자 생성....");
		if (user == null) {
			System.out.println("사용자 생성....2");
			try {
				System.out.println("여기까지 안와??");
				//ROLE_USER....
				Authentication auth = SecurityContextHolder.getContext().getAuthentication();
				System.out.println(auth.getName());
				System.out.println(auth.getCredentials());
				System.out.println(auth.getPrincipal());
				System.out.println(auth.getAuthorities());
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
				user = memberRepository.findByEmailAndPrincipal(convertUser.getEmail(),convertUser.getPrincipal());
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
				System.out.println("여기임..."+e.getMessage());
				return user;
			}
		} 
		return user;
	}

	private User convertUser(String authority, Map<String, Object> map) {
		if (FACEBOOK.getValue().equals(authority)) {
			return getModernUser(FACEBOOK, map);
		} else if (GOOGLE.getValue().equals(authority)) {
			return getModernUser(GOOGLE, map);
		}
		return null;
	}

	private User getModernUser(SocialType socialType, Map<String, Object> map) {
		System.out.println("getModernUser ::"+socialType +"    ->"+map.get("sub"));
		User user = new User();
		user.setName(String.valueOf(map.get("name")));
		user.setEmail(String.valueOf(map.get("email")));
		user.setPrincipal(map.get("id")==null?String.valueOf(map.get("sub")):String.valueOf(map.get("id")));
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
