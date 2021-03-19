package com.pb.dp.service.impl;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.pb.dp.dao.HealthLockerCallBackDao;
import com.pb.dp.model.Acknowledgement;
import com.pb.dp.model.OnNotify;
import com.pb.dp.model.Resp;
import com.pb.dp.service.ConfigService;
import com.pb.dp.service.HealthLockerCallBackService;
import com.pb.dp.util.AuthTokenUtil;
import com.pb.dp.util.HttpUtil;

/**
 * The Class HealthLockerCallBackServiceImpl.
 */
@Service
public class HealthLockerCallBackServiceImpl implements HealthLockerCallBackService {

	/** The logger. */
	private final Logger logger = LoggerFactory.getLogger(HealthLockerCallBackServiceImpl.class);

	/** The health locker call back dao. */
	@Autowired
	private HealthLockerCallBackDao healthLockerCallBackDao;
	
	/** The config service. */
	@Autowired
	private ConfigService configService;
	
	@Autowired
	private AuthTokenUtil authTokenUtil;

	/**
	 * Update call back.
	 *
	 * @param payload the payload
	 * @return true, if successful
	 * @throws Exception the exception
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean updateCallBackSubscribe(Map<String, Object> payload) throws Exception {
		boolean isUpdated = false;
		logger.info("Updating the callback for subscription for payload {} :", payload);
		if (null == payload.get("error")) {
			String requestId = (String) payload.get("requestId");
			Map<String, Object> subMap = (Map<String, Object>) payload.get("subscriptionRequest");
			Map<String, Object> respMap = (Map<String, Object>) payload.get("resp");
			String subscriptionId = (null != subMap && null != subMap.get("id")) ? (String) subMap.get("id") : null;
			String reqIdSent = (null != respMap && null != respMap.get("requestId")) ? (String) respMap.get("requestId")
					: null;
			isUpdated = healthLockerCallBackDao.updateCallBackSubscribe(requestId, subscriptionId, reqIdSent);
		}
		return isUpdated;
	}

	/**
	 * Update call back auth.
	 *
	 * @param payload the payload
	 * @return true, if successful
	 * @throws Exception the exception
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean updateCallBackAuth(Map<String, Object> payload) throws Exception {
		boolean isUpdated = false;
		logger.info("Updating the callback for Auth for payload {} :", payload);
		if (null == payload.get("error")) {
			String requestId = (String) payload.get("requestId");
			Map<String, Object> authMap = (Map<String, Object>) payload.get("auth");
			Map<String, Object> respMap = (Map<String, Object>) payload.get("resp");
			String transactionId = (null != authMap && null != authMap.get("transactionId"))
					? (String) authMap.get("transactionId")
					: null;
			String reqIdSent = (null != respMap && null != respMap.get("requestId")) ? (String) respMap.get("requestId")
					: null;
			isUpdated = healthLockerCallBackDao.updateCallBackAuth(requestId, transactionId, reqIdSent);
		}
		return isUpdated;
	}

	/**
	 * Update call back on Notify.
	 *
	 * @param payload the payload
	 * @return true, if successful
	 * @throws Exception the exception
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean updateCallBackSubscribeOnNotify(Map<String, Object> payload) throws Exception{
		boolean isUpdated = false;
		logger.info("Updating the callback for subscription for payload {} :",payload);
		if(null == payload.get("error")) {
			String requestId = (String)payload.get("requestId");
			Map<String,Object> acknowledgement = (Map<String, Object>) payload.get("acknowledgement");
			Map<String,Object> respMap = (Map<String, Object>) payload.get("resp");
			String subscriptionId = (null!=acknowledgement && "OK"==acknowledgement.get("status"))?(String)acknowledgement.get("subscriptionRequestId"):null;
			String reqIdSent = (null!=respMap && null!=respMap.get("requestId"))?(String)respMap.get("requestId"):null;
			isUpdated =  healthLockerCallBackDao.updateCallBackSubscribe(requestId,subscriptionId,reqIdSent);
		}
		return isUpdated;
	}

 	@SuppressWarnings("unchecked")
	@Override
	public boolean updateCallBackAuthNotify(Map<String, Object> payload, String hiuId) throws Exception {
		boolean isUpdated = false;
		logger.info("Updating the callback for Auth Notify with payload {} :", payload);
		Map<String, Object> authMap = (Map<String, Object>) payload.get("auth");
		String transactionId = (null != authMap && null != authMap.get("transactionId"))
				? (String) authMap.get("transactionId")
				: null;
		String accessToken = (null != authMap && null != authMap.get("accessToken")) ? (String) authMap.get("accessToken")
				: null;
		isUpdated = healthLockerCallBackDao.updateCallBackAuthNotify(transactionId, accessToken,hiuId);
		
		//call on-notify api for acknowledgment of the callback
		OnNotify onNotify = setOnNotify(payload);
		String authToken = authTokenUtil.bearerAuthToken();
		hlApiHit(authToken,onNotify,configService.getPropertyConfig("HL_AUTH_ON_NOTIFY_URL").getValue());
		
		return isUpdated;
	}
	
	private OnNotify setOnNotify(Map<String, Object> payload) {
		OnNotify onNotify = new OnNotify();
		onNotify.setRequestId(UUID.randomUUID().toString());
		onNotify.setTimestamp(Instant.now().toString());
		Acknowledgement acknowledgement = new Acknowledgement();
		acknowledgement.setStatus("OK");
		onNotify.setAcknowledgement(acknowledgement );
		Resp resp = new Resp();
		resp.setRequestId((String)payload.get("requestId"));
		onNotify.setResp(resp);
		return onNotify;
	}

	private Map<String, Object> hlApiHit(String authToken, Object object, String url) throws Exception {
		Map<String, String> header = new HashMap<>();
		header.put("Authorization", authToken);
		header.put("X-CM-ID", configService.getPropertyConfig("HL_CM_ID").getValue());
		String jsonPayload = new Gson().toJson(object);
		Map<String, Object> responseFromApi = HttpUtil.post(url, jsonPayload, header);
		return responseFromApi;
	}


}
