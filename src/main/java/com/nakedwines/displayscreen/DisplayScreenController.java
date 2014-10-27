package com.nakedwines.displayscreen;

import java.net.URI;
import java.util.Collections;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.social.support.URIBuilder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.WebRequest;

import com.fasterxml.jackson.annotation.JsonProperty;

@RestController
public class DisplayScreenController {
	@Value("${clientId}")
	private String clientId;
	@Value("${clientSecret}")
	private String clientSecret;
	@Value("${refreshToken}")
	private String refreshToken;
	
	@Autowired
	private RestTemplate restTemplate;
	
	@SuppressWarnings("unchecked")
	@RequestMapping("/realtime")
	public Map<String,Object> realtime(WebRequest request, HttpServletResponse response){
		response.addHeader("cache-control", "public, max-age=20");
		
		String accessToken = getToken();
		
		URIBuilder uriBuilder = URIBuilder.fromUri("https://www.googleapis.com/analytics/v3/data/realtime");
		uriBuilder.queryParam("access_token", accessToken);
		
		for(String key : request.getParameterMap().keySet()){
			uriBuilder.queryParam(key, request.getParameterMap().get(key)[0]);
		}
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + accessToken);

		HttpEntity<String> entity = new HttpEntity<String>("", headers);
		
		return restTemplate.exchange(uriBuilder.build(), HttpMethod.GET, entity, Map.class).getBody();
	}
	
	@RequestMapping("/feed")
	public Map<String,Object> feed(){
		return Collections.emptyMap();
	}
	
	private String getToken(){
		MultiValueMap<String, Object> request = new LinkedMultiValueMap<String, Object>();
		request.add("client_id", clientId);
		request.add("client_secret", clientSecret);
		request.add("refresh_token", refreshToken);
		request.add("grant_type", "refresh_token");
		
		
		@SuppressWarnings("unchecked")
		Map<String,Object> response = restTemplate.postForObject(URI.create("https://accounts.google.com/o/oauth2/token"), 
				request, 
				Map.class);
		return response.get("access_token").toString();
	}
}
