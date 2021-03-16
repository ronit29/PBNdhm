package com.pb.dp.service.impl;

import java.time.Instant;
import java.util.*;

import com.pb.dp.dao.HealthDocDao;
import com.pb.dp.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.pb.dp.service.ConfigService;
import com.pb.dp.service.HealthLockerService;
import com.pb.dp.util.AuthTokenUtil;
import com.pb.dp.util.HttpUtil;

/**
 * The Class HealthLockerServiceImpl.
 */
@Service
public class HealthLockerServiceImpl implements HealthLockerService{

	/** The auth token util. */
	@Autowired
	private AuthTokenUtil authTokenUtil;
	
	/** The config service. */
	@Autowired
	private ConfigService configService;

	@Autowired
	private HealthDocDao healthDocDao;


	/**
	 * Authorize.
	 *
	 * @param lockerModel the locker model
	 * @return true, if successful
	 * @throws Exception the exception
	 */
	@Override
	public boolean authorize(LockerModel lockerModel) throws Exception {
		boolean isAuthorized = false;
		String authToken = authTokenUtil.bearerAuthToken();
		Authorise authorise = setAuthorizePayload(lockerModel);
		String url = configService.getPropertyConfig("HL_AUTHORIZE_URL").getValue();
		Map<String, Object> responseFromApi = hlApiHit(authToken, authorise, url);
		int statusCode = (int) responseFromApi.get("status");
		if (statusCode == 202) {
			isAuthorized  = true;
		}
		return isAuthorized;
	}

	/**
	 * Hl api hit.
	 *
	 * @param authToken the auth token
	 * @param object the object
	 * @param url the url
	 * @return the map
	 * @throws Exception the exception
	 */
	private Map<String, Object> hlApiHit(String authToken, Object object, String url) throws Exception {
		Map<String, String> header = new HashMap<>();
		header.put("Authorization", authToken);
		header.put("X-CM-ID", configService.getPropertyConfig("HL_CM_ID").getValue());
		String jsonPayload = new Gson().toJson(object);
		Map<String, Object> responseFromApi = HttpUtil.post(url, jsonPayload, header);
		return responseFromApi;
	}

	private Map<String, Object> hiuApiHit(String authToken, Object object, String url) throws Exception {
		Map<String, String> header = new HashMap<>();
		header.put("Authorization", authToken);
		header.put("X-HIU-ID", configService.getPropertyConfig("HL_REQUESTER_LOCKER_ID").getValue());
		String jsonPayload = new Gson().toJson(object);
		Map<String, Object> responseFromApi = HttpUtil.post(url, jsonPayload, header);
		return responseFromApi;
	}

	/**
	 * Sets the authorize payload.
	 *
	 * @param lockerModel the locker model
	 * @return the authorise
	 */
	private Authorise setAuthorizePayload(LockerModel lockerModel) {
		Authorise authorise = new Authorise();
		authorise.setRequestId(UUID.randomUUID().toString());
		authorise.setTimestamp(Instant.now().toString());
		Query query = new Query();
		query.setAuthMode("DIRECT");
		query.setId(lockerModel.getHealthId());
		query.setPurpose("KYC_AND_LINK");
		Requester requester = new Requester();
		requester.setType("HEALTH_LOCKER");
		requester.setId(configService.getPropertyConfig("HL_REQUESTER_LOCKER_ID").getValue());
		query.setRequester(requester);
		authorise.setQuery(query);
		return authorise;
	}

	/**
	 * Subscribe.
	 *
	 * @param lockerModel the locker model
	 * @return true, if successful
	 * @throws Exception the exception
	 */
	@Override
	public boolean subscribe(LockerModel lockerModel) throws Exception {
		boolean isSubscribed = false;
		String authToken = authTokenUtil.bearerAuthToken();
		Subscribe subscribe = setSubscribe(lockerModel);
		String url = configService.getPropertyConfig("HL_SUBSCRIBE_URL").getValue();
		Map<String, Object> responseFromApi = hlApiHit(authToken, subscribe, url);
		int statusCode = (int) responseFromApi.get("status");
		if (statusCode == 202) {
			isSubscribed  = true;
		}
		return isSubscribed;
	}

	/**
	 * Sets the subscribe.
	 *
	 * @param lockerModel the locker model
	 * @return the subscribe
	 */
	private Subscribe setSubscribe(LockerModel lockerModel) {
		Subscribe subscribe = new Subscribe();
		subscribe.setRequestId(UUID.randomUUID().toString());
		subscribe.setTimestamp(Instant.now().toString());
		Subscription subscription = new Subscription();
		subscription.setCategories(Arrays.asList("LINK","DATA"));
		Hiu hiu = new Hiu();
		hiu.setId(configService.getPropertyConfig("HL_REQUESTER_LOCKER_ID").getValue());
		subscription.setHiu(hiu);
		Patient patient = new Patient();
		patient.setId(lockerModel.getHealthId());
		subscription.setPatient(patient);
		Period period = new Period();
		period.setFrom("2005-02-12T09:16:34.596Z");
		period.setTo("2035-02-12T09:16:34.596Z");
		subscription.setPeriod(period);
		Purpose purpose = new Purpose();
		purpose.setCode("DPLCKR");
		purpose.setRefUri("uri");
		purpose.setText("Self Requested");
		subscription.setPurpose(purpose);
		subscribe.setSubscription(subscription);
		return subscribe;
	}


	private SubscriptionNotify setSubscribeNotify(LockerModel lockerModel,String healthId) {
		Map<String, Object> data = healthDocDao.getSubscriptionData(healthId);

		SubscriptionNotify subscribeNotify = new SubscriptionNotify();
		subscribeNotify.setRequestId(UUID.randomUUID().toString());
		subscribeNotify.setTimestamp(Instant.now().toString());

		Notification notification = new Notification();
		notification.setSubscriptionRequestId((String)data.get("subscriptionId"));
		notification.setStatus("GRANTED");

		Hiu hiu = new Hiu();
		hiu.setId(configService.getPropertyConfig("HL_REQUESTER_LOCKER_ID").getValue());

		Patient patient = new Patient();
		patient.setId((String)data.get("healthId"));

		Subscription subscription = new Subscription();
		subscription.setHiu(hiu);
		subscription.setPatient(patient);

		Period period = new Period();
		period.setFrom("2005-02-12T09:16:34.596Z");
		period.setTo("2035-02-12T09:16:34.596Z");

		Hip hip = new Hip();
		hip.setId(configService.getPropertyConfig("HL_REQUESTER_LOCKER_ID").getValue());

		Sources sources = new Sources();
		sources.setCategories(Arrays.asList("LINK"));
		sources.setPeriod(period);
		sources.setHip(hip);

		subscription.setSources(sources);
		notification.setSubscription(subscription);
		return subscribeNotify;
	}

	/**
	 * subscribeNotify.
	 *
	 * @param lockerModel the locker model
	 * @param healthId the hid to be fetched
	 * @return true, if successful
	 * @throws Exception the exception
	 */
	@Override
	public boolean subscribeNotify(LockerModel lockerModel, String healthId) throws Exception {
		boolean isNotified = false;
		String authToken = authTokenUtil.bearerAuthToken();
		SubscriptionNotify subscribeNotify = setSubscribeNotify(lockerModel, healthId);
		String url = configService.getPropertyConfig("HL_SUBSCRIBE_URL").getValue();
		Map<String, Object> responseFromApi = hiuApiHit(authToken, subscribeNotify, url);
		int statusCode = (int) responseFromApi.get("status");
		if (statusCode == 202) {
			isNotified  = true;
		}
		return isNotified;
	}
}
