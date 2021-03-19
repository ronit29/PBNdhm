package com.pb.dp.service.impl;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pb.dp.dao.HealthLockerCallBackDao;
import com.pb.dp.service.HealthLockerCallBackService;

// TODO: Auto-generated Javadoc
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
		logger.info("Updating the callback for subscription for payload {} :",payload);
		if(null == payload.get("error")) {
			String requestId = (String)payload.get("requestId");
			Map<String,Object> subMap = (Map<String, Object>) payload.get("subscriptionRequest");
			Map<String,Object> respMap = (Map<String, Object>) payload.get("resp");
			String subscriptionId = (null!=subMap && null!=subMap.get("id"))?(String)subMap.get("id"):null;
			String reqIdSent = (null!=respMap && null!=respMap.get("requestId"))?(String)respMap.get("requestId"):null;
			isUpdated =  healthLockerCallBackDao.updateCallBackSubscribe(requestId,subscriptionId,reqIdSent);
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
		logger.info("Updating the callback for Auth for payload {} :",payload);
		if(null == payload.get("error")) {
			String requestId = (String)payload.get("requestId");
			Map<String,Object> authMap = (Map<String, Object>) payload.get("auth");
			Map<String,Object> respMap = (Map<String, Object>) payload.get("resp");
			String transactionId = (null!=authMap && null!=authMap.get("transactionId"))?(String)authMap.get("transactionId"):null;
			String reqIdSent = (null!=respMap && null!=respMap.get("requestId"))?(String)respMap.get("requestId"):null;
			isUpdated =  healthLockerCallBackDao.updateCallBackAuth(requestId,transactionId,reqIdSent);
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
	public boolean updateCallBackSubscribeOnNotify(Map<String, Object> payload) throws
			{
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
}
