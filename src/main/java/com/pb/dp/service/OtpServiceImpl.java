package com.pb.dp.service;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.pb.dp.dao.OtpDao;
import com.pb.dp.util.AuthTokenUtil;
import com.pb.dp.util.HttpUtil;

@Service
public class OtpServiceImpl implements OtpService {

	private final Logger logger = LoggerFactory.getLogger(OtpServiceImpl.class);
	@Autowired
	private OtpDao otpDao;
	@Autowired
	private AuthTokenUtil authTokenUtil;
	@Autowired
	private ConfigService configService;

	@Override
	public boolean isVerified(int otp, Long mobileNo, int customerId) {
		return otpDao.isVerified(otp, mobileNo, customerId);
	}

	@SuppressWarnings("unchecked")
	@Override
	public String sendNdhmOtp(long mobileNo, int customerId) throws Exception {
		logger.debug("Entered in ndhm sent otp");
		Map<String, String> header = new HashMap<>();
		String token = authTokenUtil.bearerAuthToken();
		header.put("Authorization", token);
		header.put("X-HIP-ID", "DPHIP119");
		Map<String, Object> jsonMap = new HashMap<>();
		jsonMap.put("mobile", String.valueOf(mobileNo));
		String jsonPayload = new Gson().toJson(jsonMap);
		String url = configService.getPropertyConfig("NDHM_GEN_OTP_URL").getValue();
		Map<String,Object> response = HttpUtil.post(url, jsonPayload, header);
		String responseBody = (String)response.get("responseBody");
		Map<String,Object> responseMap = (Map<String,Object>) new Gson().fromJson(responseBody, Map.class);
		String txnId = (String) responseMap.get("txnId");
		otpDao.insertTxnId(customerId,txnId);
		return txnId;
	}

	@SuppressWarnings("unchecked")
	@Override
	public String verifyNdhmOtp(String otp, String txnId) throws Exception {
		String tokenToReturn = null;
		Map<String, String> header = new HashMap<>();
		String token = authTokenUtil.bearerAuthToken();
		header.put("Authorization", token);
		header.put("X-HIP-ID", "DPHIP119");
		Map<String, Object> jsonMap = new HashMap<>();
		jsonMap.put("otp", otp);
		jsonMap.put("txnId", txnId);
		String jsonPayload = new Gson().toJson(jsonMap);
		String url = configService.getPropertyConfig("NDHM_VERIFY_OTP_URL").getValue();
		Map<String,Object> response = HttpUtil.post(url, jsonPayload, header);
		int statusCode = (int) response.get("status");
		if(statusCode == 200) {
			String responseBody = (String)response.get("responseBody");
			Map<String,Object> responseMap = (Map<String,Object>) new Gson().fromJson(responseBody, Map.class);
			tokenToReturn = (String)responseMap.get("token");
			
		}
		return tokenToReturn;
	}

}
