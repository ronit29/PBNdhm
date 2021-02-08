package com.pb.dp.service;

import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.commons.lang3.StringUtils;
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
		CustomerHealth response = healthDao.getHealthProfile(customerId,custHealthOtpRequest.getHealthId());
		SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
		if(Objects.nonNull(response.getDob())) {
			response.setDobStr(formatter.format(response.getDob()));
		}
		if (null != custHealthOtpRequest.getHealthId() && !custHealthOtpRequest.getHealthId().isEmpty()) {
			StringBuilder xToken = new StringBuilder("Bearer ");
			String authToken = healthDao.getHealthToken(custHealthOtpRequest.getHealthId());
			String token = authTokenUtil.bearerAuthToken();
			if (null != authToken) {
				isValidBoolean = authTokenUtil.isValidToken(authToken, token);
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
				} else {
					String txnId = getTxnId(token, custHealthOtpRequest.getHealthId());
					response.setTxnId(txnId);
				}
			}
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private String getTxnId(String token, String healthId) throws Exception {
		String txnId = null;
		Map<String, String> header = new HashMap<>();
		header.put("Authorization", token);
		header.put("X-HIP-ID", "DPHIP119");
		String url = configService.getPropertyConfig("NDHM_AUTH_INIT_URL").getValue();
		Map<String, Object> jsonMap = new HashMap<>();
		jsonMap.put("authMethod", "MOBILE_OTP");
		jsonMap.put("healthid", healthId);
		String jsonPayload = new Gson().toJson(jsonMap);
		Map<String, Object> responseFromApi = HttpUtil.post(url, jsonPayload, header);
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
					String url = configService.getPropertyConfig("NDHM_PNG_CARD_URL").getValue();
					Map<String, Object> responseFromApi = HttpUtil.getContentByteByURLWithHeader(url, header);
					if (null != responseFromApi.get("Bytes")) {
						byte[] qrByteArray = (byte[]) responseFromApi.get("Bytes");
						byteStringCard = Base64.getEncoder().encodeToString(qrByteArray);
						healthDao.updateCard(byteStringCard, custHealthOtpRequest.getHealthId());
					}
				}
			}
		}

		return byteStringCard;

	}

}
