package com.cjhm.member.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cjhm.annotation.SocialUser;
import com.cjhm.member.constants.MemberConstants;
import com.cjhm.member.entity.User;
import com.cjhm.member.enums.SocialType;
import com.cjhm.member.service.MemberService;

import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;

@Controller
public class LoginController {

	Logger logger = LoggerFactory.getLogger(LoginController.class);

	@Autowired
	private MemberService memberService;
	/**
	 * UserArgumentResolver가 역할을 수행함
	 * google과 facebook만 적용 
	 * @param user
	 * @return
	 */
	@GetMapping("/loginSuccess")
	public String loginSuccess(@SocialUser User user) {
		return "redirect:/";
	}
	String baseUrl="http://127.0.0.1:8080";
	
	@GetMapping("/loginSuccess/kakao")
	public String kakaoLogin(HttpServletRequest request,@Value("${custom.oauth2.kakao.client-id}") String clientId
			,HttpSession session) throws UnsupportedEncodingException {
		String code = request.getParameter("code");
		String state= request.getParameter("state");
		String tokenUri="https://kauth.kakao.com/oauth/token";
		String redirectURI=baseUrl+"/loginSuccess/kakao";
		JSONObject token = null;
		try {
			logger.debug("code="+code+", state="+state);
			token = authLogin(tokenUri,code,state,clientId,"x",redirectURI);
		} catch (IOException e) {
			e.printStackTrace();
			request.setAttribute("error", e.getMessage());
			return "/member/login";
		}
		String accessToken = null;
//		String refreshToken;
		if(token!=null) {
			accessToken = (String)token.get("access_token");
//			refreshToken = (String)token.get("refresh_token");
		}else {
			request.setAttribute("error", "접속 오류");
			return "/member/login";
		}

		String apiURI="https://kapi.kakao.com/v1/user/me";

		JSONObject dataInfo = getAuthProfile(apiURI,accessToken);
		JSONObject properties = (JSONObject) dataInfo.get("properties");
		String id = String.valueOf((Long)dataInfo.get("id"));
		String email = (String)dataInfo.get("kaccount_email");
		String name = (String)properties.get("nickname");
		
		User user = new User();
		user.setPrincipal(id);
		user.setEmail(email);
		user.setName(name);
		user.setSocialType(SocialType.KAKAO);

		saveUserSession(user,session);

		return "redirect:/";
	}
	@GetMapping("/loginSuccess/naver")
	public String naverLogin(HttpServletRequest request, @Value("${custom.oauth2.naver.client-id}") String clientId,
			@Value("${custom.oauth2.naver.client-secret}") String clientSecret
			,HttpSession session) throws IOException {
		// 네이버 상태 체크
		String code = request.getParameter("code");
		String state = request.getParameter("state");
		String tokenUri = "https://nid.naver.com/oauth2.0/token";
		String redirectURI = baseUrl+"/loginSuccess/naver";
		JSONObject token = null;
			token = authLogin(tokenUri, code, state, clientId, clientSecret, redirectURI);
		String accessToken = null;
		String apiURI = "https://openapi.naver.com/v1/nid/me";
//		String refreshToken;
		if (token != null) {
			accessToken = (String) token.get("access_token");
//			refreshToken = (String) token.get("refresh_token");
		}else {
			request.setAttribute("error", "접속 오류");
			return "/member/login";
		}

		JSONObject dataInfo = getAuthProfile(apiURI, accessToken);
		JSONObject respJson = (JSONObject) dataInfo.get("response");
		String id = respJson.get("id").toString();
		String nickname = respJson.get("nickname").toString();
		String email = respJson.get("email").toString();
		String name = respJson.get("name").toString();
		System.out.println(id + "/" + nickname + "/" + email + "/" + name);
		User user = new User();
		user.setPrincipal(id);
		user.setEmail(email);
		user.setName(name);
		user.setSocialType(SocialType.NAVER);

		saveUserSession(user,session);
		return "redirect:/";
	}
	
	
	private User saveUserSession(User user, HttpSession session) {
		User sessionUser = (User) session.getAttribute(MemberConstants.SESSION_USER);
		User saveUser = null;
		if (sessionUser == null) {
			saveUser = memberService.findAuthUserByEmail(user.getEmail(),user.getPrincipal());
			if (saveUser == null) {
				saveUser = memberService.saveUser(user);
			}
//			OAuth2AuthenticationToken authentication = (OAuth2AuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

//			Map<String,Object> map = authentication.getPrincipal().getAttributes();
//			System.out.println("getUser Map info...START");
//			for(String o : map.keySet()) {
//				System.out.println(o+" : "+map.get(o));
//			}

			setRoleIfNotSame(user,null);
			session.setAttribute(MemberConstants.SESSION_USER, saveUser);
			System.out.println(saveUser);
			return saveUser;
		} else {
			System.out.println("계정이 이미 존재...");
			return sessionUser;
		}
	}
	private void setRoleIfNotSame(User user,Map<String,Object> map) {
		SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(map,"N/A",
				AuthorityUtils.createAuthorityList(user.getSocialType().getRoleType())));
		
	}
	private JSONObject getAuthProfile(String apiURI , String accessToken) {
        String header = "Bearer " + accessToken; // Bearer 다음에 공백 추가
        try {
            String apiURL = apiURI;
            URL url = new URL(apiURL);
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Authorization", header);
            int responseCode = con.getResponseCode();
  	      BufferedReader br;
            if(responseCode==200) { // 정상 호출
            	br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            } else {  // 에러 발생
            	br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
            }
            StringBuffer response = new StringBuffer();
            String inputLine;
			while ((inputLine = br.readLine()) != null) {
                response.append(inputLine);
            }
			br.close();
			logger.debug("callback getAuthProfile data : "+responseCode+" - "+response.toString());
            
  	      if(responseCode==200) {
	    	  JSONParser parser = new JSONParser(JSONParser.MODE_JSON_SIMPLE);
	    	  JSONObject json=(JSONObject)parser.parse(response.toString());
	    	  return json;
	      }
  	      
        } catch (Exception e) {
        	System.out.println("여긴가..???");
            System.out.println(e);
        }
        
		return null;
	}

	private JSONObject authLogin(String tokenUri,String code, String state, String clientId, String clientSecret,String redirectURI) throws UnsupportedEncodingException {
		String apiURL = "";
		redirectURI = URLEncoder.encode(redirectURI, "UTF-8");

		apiURL = tokenUri+"?grant_type=authorization_code";
		apiURL += "&client_id=" + clientId;
		apiURL += "&client_secret=" + clientSecret;
		apiURL += "&redirect_uri=" + redirectURI;
		apiURL += "&code=" + code;
		apiURL += "&state=" + state;
		logger.debug("apiURL= " + apiURL);
		try {
			BufferedReader br;
			URL url = new URL(apiURL);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			int responseCode = con.getResponseCode();
			logger.debug("responseCode=" + responseCode);
			if (responseCode == 200) { // 정상 호출
				br = new BufferedReader(new InputStreamReader(con.getInputStream()));
			} else { // 에러 발생
				br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
			}
			String inputLine;
			StringBuffer res = new StringBuffer();
			while ((inputLine = br.readLine()) != null) {
				res.append(inputLine);
			}
			br.close();
			logger.debug("callback authLogin data : "+responseCode+" - "+res.toString());
			if (responseCode == 200) {
				JSONParser parser = new JSONParser(JSONParser.MODE_JSON_SIMPLE);
				JSONObject json = (JSONObject) parser.parse(res.toString());
				return json;
			}
		} catch (IOException e) {
			e.printStackTrace();
			// TODO Auto-generated catch block
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	@PostMapping("/loginSuccess")
	public String postloginSuccess(@SocialUser User user) {
		return "redirect:/";
	}
	@GetMapping("/loginFailure")
	public String loginFailure() {
		return "redirect:/";
	}
	
	@GetMapping("/facebook")
	@ResponseBody
	public String facebook() {
		return "facebook";
	}
	@GetMapping("/kakao")
	@ResponseBody
	public String kakao() {
		return "kakao";
	}
	@GetMapping("/google")
	@ResponseBody
	public String google() {
		return "google";
	}
	
}
