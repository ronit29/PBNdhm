package com.pb.dp.web.controller;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.pb.dp.enums.ResponseStatus;
import com.pb.dp.model.FieldKey;
import com.pb.dp.service.HealthLockerCallBackService;

/**
 * The Class HealthLockerCallBackController.
 */
@RestController
@RequestMapping(value = "/v0.5")
public class HealthLockerCallBackController {

	/** The logger. */
	private final Logger logger = LoggerFactory.getLogger(HealthLockerCallBackController.class);

	/** The health locker call back service. */
	@Autowired
	private HealthLockerCallBackService healthLockerCallBackService;

	/**
	 * Subscribe.
	 *
	 * @param hiuId the hiu id
	 * @param authorization the authorization
	 * @param payload the payload
	 * @return the response entity
	 */
	@RequestMapping(value = "/subscription-requests/hiu/on-init", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<Map<String, Object>> subscribe(@RequestHeader(value = "X-HIU-ID") String hiuId,
			@RequestHeader(value = "Authorization") String authorization, @RequestBody Map<String, Object> payload) {

		HttpStatus status = HttpStatus.OK;
		Map<String, Object> response = new HashMap<>();
		try {
			logger.debug("Callback for subscribe with payload :{} and X-HIU-ID : {}",payload,hiuId);
			boolean isupdated = healthLockerCallBackService.updateCallBackSubscribe(payload);
			if (isupdated) {
				response.put(FieldKey.SK_STATUS_MESSAGE, ResponseStatus.SUCCESS.getStatusMsg());
				response.put(FieldKey.SK_STATUS_CODE, ResponseStatus.SUCCESS.getStatusId());
			} else {
				response.put(FieldKey.SK_STATUS_MESSAGE, ResponseStatus.FAILURE.getStatusMsg());
				response.put(FieldKey.SK_STATUS_CODE, ResponseStatus.FAILURE.getStatusId());
			}
		} catch (Exception e) {
			logger.debug(e.getMessage());
			status = HttpStatus.INTERNAL_SERVER_ERROR;
			response.put(FieldKey.SK_STATUS_CODE, ResponseStatus.FAILURE.getStatusId());
			response.put(FieldKey.SK_STATUS_MESSAGE, e.getMessage());
		}

		return new ResponseEntity<>(response, status);
	}
	
	/**
	 * Auth.
	 *
	 * @param hiuId the hiu id
	 * @param authorization the authorization
	 * @param payload the payload
	 * @return the response entity
	 */
	@RequestMapping(value = "/users/auth/on-init", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<Map<String, Object>> auth(@RequestHeader(value = "X-HIU-ID") String hiuId,
			@RequestHeader(value = "Authorization") String authorization, @RequestBody Map<String, Object> payload) {

		HttpStatus status = HttpStatus.OK;
		Map<String, Object> response = new HashMap<>();
		logger.debug("Callback for user auth with payload :{} and X-HIU-ID : {}",payload,hiuId);
		try {
			boolean isupdated = healthLockerCallBackService.updateCallBackAuth(payload);
			if (isupdated) {
				response.put(FieldKey.SK_STATUS_MESSAGE, ResponseStatus.SUCCESS.getStatusMsg());
				response.put(FieldKey.SK_STATUS_CODE, ResponseStatus.SUCCESS.getStatusId());
			} else {
				response.put(FieldKey.SK_STATUS_MESSAGE, ResponseStatus.FAILURE.getStatusMsg());
				response.put(FieldKey.SK_STATUS_CODE, ResponseStatus.FAILURE.getStatusId());
			}
		} catch (Exception e) {
			logger.debug(e.getMessage());
			status = HttpStatus.INTERNAL_SERVER_ERROR;
			response.put(FieldKey.SK_STATUS_CODE, ResponseStatus.FAILURE.getStatusId());
			response.put(FieldKey.SK_STATUS_MESSAGE, e.getMessage());
		}

		return new ResponseEntity<>(response, status);
	}
	
	/**
	 * Auth notify.
	 *
	 * @param hiuId the hiu id
	 * @param authorization the authorization
	 * @param payload the payload
	 * @return the response entity
	 */
	@RequestMapping(value = "/users/auth/notify", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<Map<String, Object>> authNotify(@RequestHeader(value = "X-HIU-ID") String hiuId,
			@RequestHeader(value = "Authorization") String authorization, @RequestBody Map<String, Object> payload) {

		HttpStatus status = HttpStatus.OK;
		Map<String, Object> response = new HashMap<>();
		logger.debug("Callback for user auth with payload :{} and X-HIU-ID : {}",payload,hiuId);
		try {
			boolean isupdated = healthLockerCallBackService.updateCallBackAuthNotify(payload,hiuId);
			if (isupdated) {
				response.put(FieldKey.SK_STATUS_MESSAGE, ResponseStatus.SUCCESS.getStatusMsg());
				response.put(FieldKey.SK_STATUS_CODE, ResponseStatus.SUCCESS.getStatusId());
			} else {
				response.put(FieldKey.SK_STATUS_MESSAGE, ResponseStatus.FAILURE.getStatusMsg());
				response.put(FieldKey.SK_STATUS_CODE, ResponseStatus.FAILURE.getStatusId());
			}
		} catch (Exception e) {
			logger.debug(e.getMessage());
			status = HttpStatus.INTERNAL_SERVER_ERROR;
			response.put(FieldKey.SK_STATUS_CODE, ResponseStatus.FAILURE.getStatusId());
			response.put(FieldKey.SK_STATUS_MESSAGE, e.getMessage());
		}

		return new ResponseEntity<>(response, status);
	}
}
