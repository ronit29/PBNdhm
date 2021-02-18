package com.pb.dp.util;

import com.google.gson.Gson;
import com.pb.dp.dao.HealthDao;
import com.pb.dp.healthIdCreation.model.NdhmMobOtpRequest;
import com.pb.dp.service.ConfigService;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Component
public class AuthTokenUtil {

    @Autowired
    private ConfigService configService;

    @Autowired
    private LoggerUtil loggerUtil;

    @Autowired
    private HealthDao healthDao;

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

    public String getValidToken(NdhmMobOtpRequest ndhmMobOtpRequest, String authToken) throws Exception {
        String token = null;
        boolean isValidToken = true;
        StringBuilder xToken = new StringBuilder("Bearer ");
        String oldToken = healthDao.getHealthToken(ndhmMobOtpRequest.getHealthId());
        if (null != oldToken) {
            isValidToken = isValidToken(oldToken, token);
            if (isValidToken) {
                xToken.append(oldToken);
            } else {
                xToken.append(this.confirmWithOtp(ndhmMobOtpRequest, authToken));
            }
        }else {
            xToken.append(this.confirmWithOtp(ndhmMobOtpRequest, authToken));
        }

        token = xToken.toString();
        return token;
    }

    private String confirmWithOtp(NdhmMobOtpRequest ndhmMobOtpRequest, String authToken) throws Exception {
        Map<String, Object> response = new HashMap<>();
        String token = null;
        String url = configService.getPropertyConfig("NDHM_CONFIRM_MOB_OTP_URL").getValue();
        Map<String, String> headers = new HashMap<>();
        headers.put("X-HIP-ID", "DPHIP119");
        headers.put("Authorization", authToken);
        Map<String, Object> payload = new HashMap<>();
        payload.put("otp", String.valueOf(ndhmMobOtpRequest.getOtp()));
        payload.put("txnId", ndhmMobOtpRequest.getTxnId());
        String jsonPayload = new Gson().toJson(payload);
        response = HttpUtil.post(url, jsonPayload, headers);
        loggerUtil.logApiData(url,jsonPayload,headers,response);
        if(Objects.nonNull(response)) {
            Object responseBody = response.get("responseBody");
            if(Objects.nonNull(responseBody) && response.get("status").equals(200)) {
                Map<String, Object> responseBodyMap = new Gson().fromJson(responseBody.toString(), Map.class);
                if(ObjectUtils.isNotEmpty(responseBodyMap.get("token"))) {
                    token = (String) responseBodyMap.get("token");
                }
            }
        }
        return token;
    }
}
