package com.pb.dp.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.pb.dp.dao.HealthDao;
import com.pb.dp.model.CustomerHealth;
import com.pb.dp.model.GetHealthProfileRequest;
import com.pb.dp.util.AuthTokenUtil;
import com.pb.dp.util.HttpUtil;

@Service
public class HealthServiceImpl implements HealthService {

	@Autowired
	private HealthDao healthDao;
	
	@Autowired
	private ConfigService configService;
	
	@Autowired
	private AuthTokenUtil authTokenUtil;
	
	@Override
	public List<CustomerHealth> getCustHealthDetails(long mobileNo, int customerId) {
		return healthDao.getCustHealthDetails(mobileNo,customerId);
	}

	@SuppressWarnings("unchecked")
	@Override
	public CustomerHealth getHealthProfile(int customerId, GetHealthProfileRequest custHealthOtpRequest)
			throws Exception {
		CustomerHealth response = healthDao.getHealthProfile(customerId);
		if(null!=custHealthOtpRequest.getHealthId() && !custHealthOtpRequest.getHealthId().isEmpty()) {
			StringBuilder xToken = new StringBuilder("Bearer "); 
			String authToken = healthDao.getHealthToken(custHealthOtpRequest.getHealthId());
			if (null != authToken) {
				Map<String, String> header = new HashMap<>();
				String token = authTokenUtil.bearerAuthToken();
				header.put("Authorization", token);
				header.put("X-HIP-ID", "DPHIP119");
				Map<String, Object> jsonMap = new HashMap<>();
				jsonMap.put("authToken", authToken);
				String jsonPayload = new Gson().toJson(jsonMap);
				String url = configService.getPropertyConfig("NDHM_ACCOUNT_TOKEN_URL").getValue();
				Map<String,Object> responseFromApi = HttpUtil.post(url, jsonPayload, header);
				int statusCode = (int) responseFromApi.get("status");
				if(statusCode == 200) {
					String responseBody = (String)responseFromApi.get("responseBody");
					Boolean isValidBoolean = Boolean.valueOf(responseBody);
					if(isValidBoolean) {
						xToken.append(authToken);
						Map<String, String> header2 = new HashMap<>();
						header2.put("Authorization", token);
						header2.put("X-HIP-ID", "DPHIP119");
						header2.put("X-Token", xToken.toString());
						String url2 = configService.getPropertyConfig("NDHM_ACCOUNT_PROFILE_URL").getValue();
						Map<String,Object> responseFromApi2 = HttpUtil.post(url2, jsonPayload, header2);
						int statusCode2 = (int) responseFromApi2.get("status");
						if(statusCode2 == 200) {
							String responseBody2 = (String)responseFromApi2.get("responseBody");
							Map<String,Object> responseMap = (Map<String,Object>) new Gson().fromJson(responseBody2, Map.class);
							if(null!=responseMap) {
								response.setAddress((String)responseMap.get("address"));
								response.setState((String)responseMap.get("stateName"));
								response.setDistrict((String)responseMap.get("districtName"));
								response.setHealthId((String)responseMap.get("healthId"));
								response.setHealtIdNo((String)responseMap.get("healthIdNumber"));
								response.setEmailId((String)responseMap.get("email"));
								response.setGender((String)responseMap.get("gender"));
								response.setFirstName((String)responseMap.get("firstName"));
								response.setMidName((String)responseMap.get("middleName"));
								response.setLastName((String)responseMap.get("lastName"));
							}
						}
					}
				}
			}
		}
		return response;
	}

}
