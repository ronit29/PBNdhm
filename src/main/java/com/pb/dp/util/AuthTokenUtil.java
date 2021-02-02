package com.pb.dp.util;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.pb.dp.service.ConfigService;

@Component
public class AuthTokenUtil {
	

	@Autowired
	private ConfigService configService;
	
	@SuppressWarnings("unchecked")
	public String bearerAuthToken() throws Exception {
		String clientId = configService.getPropertyConfig("NDHM_CLIENT_ID").getValue();
		String clientSecret = configService.getPropertyConfig("NDHM_CLIENT_SECRET").getValue();
		String url = configService.getPropertyConfig("NDHM_AUTH_URL").getValue();
		Gson gson = new Gson();
		Map<String,Object> payLoad = new HashMap<>();
		payLoad.put("clientId", clientId);
		payLoad.put("clientSecret", clientSecret);
		String jsonPayload = gson.toJson(payLoad);
		Map<String, Object> response = HttpUtil.post(url, jsonPayload, null);
		StringBuilder authToken = new StringBuilder("Bearer ");
		String responseMapString = (String)response.get("responseBody");
		Map<String,Object> responseMap = (Map<String,Object>) gson.fromJson(responseMapString, Map.class);
		String token = (String)responseMap.get("accessToken");
		authToken.append(token);
		return authToken.toString();
	}

}
