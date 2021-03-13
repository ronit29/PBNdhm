package com.pb.dp.service.impl;

import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.pb.dp.model.Authorise;
import com.pb.dp.model.Hiu;
import com.pb.dp.model.LockerModel;
import com.pb.dp.model.Patient;
import com.pb.dp.model.Period;
import com.pb.dp.model.Purpose;
import com.pb.dp.model.Query;
import com.pb.dp.model.Requester;
import com.pb.dp.model.Subscribe;
import com.pb.dp.model.Subscription;
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
	
}
