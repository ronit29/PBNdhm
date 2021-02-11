package com.pb.dp.util;

import com.google.gson.Gson;
import com.pb.dp.service.ConfigService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class AuthTokenUtil {

    @Autowired
    private ConfigService configService;

    @Autowired
    LoggerUtil loggerUtil;

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
    
    public boolean isValidToken(String authToken, String token) throws Exception {
		Map<String, String> header = new HashMap<>();
		header.put("Authorization", token);
		header.put("X-HIP-ID", "DPHIP119");
		Map<String, Object> jsonMap = new HashMap<>();
		jsonMap.put("authToken", authToken);
		String jsonPayload = new Gson().toJson(jsonMap);
		String url = configService.getPropertyConfig("NDHM_ACCOUNT_TOKEN_URL").getValue();
		Map<String, Object> responseFromApi = HttpUtil.post(url, jsonPayload, header);
        //loggerUtil.logApiData(url,jsonPayload,header,responseFromApi);
		int statusCode = (int) responseFromApi.get("status");
		Boolean isValidBoolean = false;
		if (statusCode == 200) {
			String responseBody = (String) responseFromApi.get("responseBody");
			isValidBoolean  = Boolean.valueOf(responseBody);
		}
		return isValidBoolean;
	}

    public String authInit(String healthId) throws Exception {
        String txnId = null;
        Map<String, String> header = new HashMap<>();
        header.put("Authorization", this.bearerAuthToken());
        header.put("X-HIP-ID", "DPHIP119");
        String url = configService.getPropertyConfig("NDHM_AUTH_INIT_URL").getValue();
        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("authMethod", "MOBILE_OTP");
        jsonMap.put("healthid", healthId);
        String jsonPayload = new Gson().toJson(jsonMap);
        Map<String, Object> responseFromApi = HttpUtil.post(url, jsonPayload, header);
        //loggerUtil.logApiData(url,jsonPayload,header,responseFromApi);
        int statusCode2 = (int) responseFromApi.get("status");
        if (statusCode2 == 200) {
            String responseBody = (String) responseFromApi.get("responseBody");
            Map<String, Object> responseMap = (Map<String, Object>) new Gson().fromJson(responseBody, Map.class);
            txnId = (String) responseMap.get("txnId");
        }else {
            txnId = StringUtils.EMPTY;
        }
        return txnId;
    }
}
