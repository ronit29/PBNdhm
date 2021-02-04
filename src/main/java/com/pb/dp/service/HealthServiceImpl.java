package com.pb.dp.service;

import java.util.Base64;
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
		return healthDao.getCustHealthDetails(mobileNo, customerId);
	}

	@SuppressWarnings("unchecked")
	@Override
	public CustomerHealth getHealthProfile(int customerId, GetHealthProfileRequest custHealthOtpRequest)
			throws Exception {
		boolean isValidBoolean = true;
		CustomerHealth response = healthDao.getHealthProfile(customerId);
		if (null != custHealthOtpRequest.getHealthId() && !custHealthOtpRequest.getHealthId().isEmpty()) {
			StringBuilder xToken = new StringBuilder("Bearer ");
			String authToken = healthDao.getHealthToken(custHealthOtpRequest.getHealthId());
			String token = authTokenUtil.bearerAuthToken();
			if (null != authToken) {
				//isValidBoolean = authTokenUtil.isValidToken(authToken, token);
				if (isValidBoolean) {
					xToken.append(authToken);
					setQrCodeDetails(response, token, xToken.toString(), custHealthOtpRequest.getHealthId());
					Map<String, String> header2 = new HashMap<>();
					header2.put("Authorization", token);
					header2.put("X-HIP-ID", "DPHIP119");
					header2.put("X-Token", xToken.toString());
					String url2 = configService.getPropertyConfig("NDHM_ACCOUNT_PROFILE_URL").getValue();
					Map<String, Object> jsonMap = new HashMap<>();
					String jsonPayload = new Gson().toJson(jsonMap);
					Map<String, Object> responseFromApi2 = HttpUtil.post(url2, jsonPayload, header2);
					int statusCode2 = (int) responseFromApi2.get("status");
					if (statusCode2 == 200) {
						String responseBody2 = (String) responseFromApi2.get("responseBody");
						Map<String, Object> responseMap = (Map<String, Object>) new Gson().fromJson(responseBody2,
								Map.class);
						if (null != responseMap) {
							response.setAddress((String) responseMap.get("address"));
							response.setState((String) responseMap.get("stateName"));
							response.setDistrict((String) responseMap.get("districtName"));
							response.setHealthId((String) responseMap.get("healthId"));
							response.setHealtIdNo((String) responseMap.get("healthIdNumber"));
							response.setEmailId((String) responseMap.get("email"));
							response.setGender((String) responseMap.get("gender"));
							response.setFirstName((String) responseMap.get("firstName"));
							response.setMidName((String) responseMap.get("middleName"));
							response.setLastName((String) responseMap.get("lastName"));
						}
					}
				}
			}
		}
		return response;
	}

	private void setQrCodeDetails(CustomerHealth response, String token, String xToken, String healthId)
			throws Exception {
		Map<String, String> header = new HashMap<>();
		header.put("Authorization", token);
		header.put("X-HIP-ID", "DPHIP119");
		header.put("X-Token", xToken);
		String url = configService.getPropertyConfig("NDHM_QR_CODE_URL").getValue();
		Map<String, Object> responseFromApi = HttpUtil.getContentByteByURLWithHeader(url, header);
		if (null != responseFromApi.get("Bytes")) {
			byte[] qrByteArray = (byte[]) responseFromApi.get("Bytes");
			String qrCode = Base64.getEncoder().encodeToString(qrByteArray);
			response.setQrCode(Base64.getEncoder().encodeToString(qrByteArray));
			healthDao.updateQrCode(qrCode, healthId);
		}

	}

	@Override
	public String getCardContent(int customerId, GetHealthProfileRequest custHealthOtpRequest) throws Exception {
		String byteStringCard = null;
		boolean isValidBoolean = true;
		if (null != custHealthOtpRequest.getHealthId() && !custHealthOtpRequest.getHealthId().isEmpty()) {
			StringBuilder xToken = new StringBuilder("Bearer ");
			String authToken = healthDao.getHealthToken(custHealthOtpRequest.getHealthId());
			String token = authTokenUtil.bearerAuthToken();
			if (null != authToken) {
				// isValidBoolean = authTokenUtil.isValidToken(authToken, token);
				if (isValidBoolean) {
					xToken.append(authToken);
					Map<String, String> header = new HashMap<>();
					header.put("Authorization", token);
					header.put("X-HIP-ID", "DPHIP119");
					header.put("X-Token", xToken.toString());
					String url2 = configService.getPropertyConfig("NDHM_SVG_CARD_URL").getValue();
					Map<String, Object> jsonMap = new HashMap<>();
					String jsonPayload = new Gson().toJson(jsonMap);
					Map<String, Object> responseFromApi = HttpUtil.post(url2, jsonPayload, header);
					int statusCode = (int) responseFromApi.get("status");
					if (statusCode == 200) {
						byteStringCard = (String) responseFromApi.get("responseBody");
						healthDao.updateCard(byteStringCard, custHealthOtpRequest.getHealthId());
					}
				}
			}
		}

		return byteStringCard;

	}

}
