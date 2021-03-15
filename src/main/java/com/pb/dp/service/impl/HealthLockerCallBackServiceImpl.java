package com.pb.dp.service.impl;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pb.dp.dao.HealthLockerCallBackDao;
import com.pb.dp.service.HealthLockerCallBackService;

/**
 * The Class HealthLockerCallBackServiceImpl.
 */
@Service
public class HealthLockerCallBackServiceImpl implements HealthLockerCallBackService {

	/** The logger. */
	private final Logger logger = LoggerFactory.getLogger(HealthLockerCallBackServiceImpl.class);

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
			Map<String,Object> subMap = (Map<String, Object>) payload.get("requestId");
			Map<String,Object> respMap = (Map<String, Object>) payload.get("resp");
			String subscriptionId = (null!=subMap && null!=subMap.get("id"))?(String)subMap.get("id"):null;
			String reqIdSent = (null!=respMap && null!=respMap.get("requestId"))?(String)respMap.get("requestId"):null;
			isUpdated =  healthLockerCallBackDao.updateCallBackSubscribe(requestId,subscriptionId,reqIdSent);
		}
		return isUpdated;
	}

}
