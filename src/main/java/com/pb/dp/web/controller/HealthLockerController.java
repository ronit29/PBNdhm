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
import com.pb.dp.model.AuthDetail;
import com.pb.dp.model.FieldKey;
import com.pb.dp.model.LockerModel;
import com.pb.dp.service.ConfigService;
import com.pb.dp.service.HealthLockerService;
import com.pb.dp.util.AES256Cipher;

/**
 * The Class HealthLockerController.
 */
@RestController
@RequestMapping(value = "/locker")
public class HealthLockerController {

	/** The logger. */
	private final Logger logger = LoggerFactory.getLogger(HealthLockerController.class);

	/** The config service. */
	@Autowired
	private ConfigService configService;

	/** The health locker service. */
	@Autowired
	private HealthLockerService healthLockerService;

	/**
	 * Authorize.
	 *
	 * @param clientKey   the client key
	 * @param authKey     the auth key
	 * @param custId      the cust id
	 * @param lockerModel the locker model
	 * @return the response entity
	 */
	@RequestMapping(value = "/authorize", method = RequestMethod.POST, produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<Map<String, Object>> authorize(@RequestHeader(value = "X-CLIENT-KEY") String clientKey,
			@RequestHeader(value = "X-AUTH-KEY") String authKey, @RequestHeader(value = "X-CID") String custId,
			@RequestBody LockerModel lockerModel) {

		HttpStatus status = HttpStatus.OK;
		Map<String, Object> response = new HashMap<>();
		try {
			if (clientKey != null && !clientKey.isEmpty()) {
				AuthDetail authDetail = configService.getAuthDetail(clientKey);
				if (authDetail == null) {
					response.put(FieldKey.SK_STATUS_MESSAGE, ResponseStatus.INVALID_CLIENT_KEY.getStatusMsg());
					response.put(FieldKey.SK_STATUS_CODE, ResponseStatus.INVALID_CLIENT_KEY.getStatusId());
					return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
				}
				if (authDetail.getAuth_key().equals(authKey)) {
					AES256Cipher cipher = configService.getAESForClientKeyMap(clientKey);
					try {
						Integer.valueOf(cipher.decrypt(custId));
						boolean isAuthorize = healthLockerService.authorize(lockerModel);
						if (isAuthorize) {
							response.put(FieldKey.SK_STATUS_MESSAGE, ResponseStatus.SUCCESS.getStatusMsg());
							response.put(FieldKey.SK_STATUS_CODE, ResponseStatus.SUCCESS.getStatusId());
						} else {
							response.put(FieldKey.SK_STATUS_MESSAGE, ResponseStatus.FAILURE.getStatusMsg());
							response.put(FieldKey.SK_STATUS_CODE, ResponseStatus.FAILURE.getStatusId());
						}

					} catch (NumberFormatException exception) {
						response.put(FieldKey.SK_STATUS_MESSAGE, ResponseStatus.INVALID_FORMAT_PARAM.getStatusMsg()
								+ " Reason: customerId must be a number");
						response.put(FieldKey.SK_STATUS_CODE, ResponseStatus.FAILURE.getStatusId());
					}

				} else {
					response.put(FieldKey.SK_STATUS_MESSAGE, ResponseStatus.INVALID_AUTH_KEY.getStatusMsg());
					response.put(FieldKey.SK_STATUS_CODE, ResponseStatus.INVALID_AUTH_KEY.getStatusId());
					status = HttpStatus.UNAUTHORIZED;
				}
			} else {
				response.put(FieldKey.SK_STATUS_MESSAGE, ResponseStatus.INVALID_CLIENT_KEY.getStatusMsg() + " Empty");
				response.put(FieldKey.SK_STATUS_CODE, ResponseStatus.INVALID_CLIENT_KEY.getStatusId());
				status = HttpStatus.UNAUTHORIZED;
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
	 * Subscribe.
	 *
	 * @param clientKey   the client key
	 * @param authKey     the auth key
	 * @param custId      the cust id
	 * @param lockerModel the locker model
	 * @return the response entity
	 */
	@RequestMapping(value = "/subscribe", method = RequestMethod.POST, produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<Map<String, Object>> subscribe(@RequestHeader(value = "X-CLIENT-KEY") String clientKey,
			@RequestHeader(value = "X-AUTH-KEY") String authKey, @RequestHeader(value = "X-CID") String custId,
			@RequestBody LockerModel lockerModel) {

		HttpStatus status = HttpStatus.OK;
		Map<String, Object> response = new HashMap<>();
		try {
			if (clientKey != null && !clientKey.isEmpty()) {
				AuthDetail authDetail = configService.getAuthDetail(clientKey);
				if (authDetail == null) {
					response.put(FieldKey.SK_STATUS_MESSAGE, ResponseStatus.INVALID_CLIENT_KEY.getStatusMsg());
					response.put(FieldKey.SK_STATUS_CODE, ResponseStatus.INVALID_CLIENT_KEY.getStatusId());
					return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
				}
				if (authDetail.getAuth_key().equals(authKey)) {
					AES256Cipher cipher = configService.getAESForClientKeyMap(clientKey);
					try {
						Integer.valueOf(cipher.decrypt(custId));
						boolean isAuthorize = healthLockerService.subscribe(lockerModel);
						if (isAuthorize) {
							response.put(FieldKey.SK_STATUS_MESSAGE, ResponseStatus.SUCCESS.getStatusMsg());
							response.put(FieldKey.SK_STATUS_CODE, ResponseStatus.SUCCESS.getStatusId());
						} else {
							response.put(FieldKey.SK_STATUS_MESSAGE, ResponseStatus.FAILURE.getStatusMsg());
							response.put(FieldKey.SK_STATUS_CODE, ResponseStatus.FAILURE.getStatusId());
						}

					} catch (NumberFormatException exception) {
						response.put(FieldKey.SK_STATUS_MESSAGE, ResponseStatus.INVALID_FORMAT_PARAM.getStatusMsg()
								+ " Reason: customerId must be a number");
						response.put(FieldKey.SK_STATUS_CODE, ResponseStatus.FAILURE.getStatusId());
					}

				} else {
					response.put(FieldKey.SK_STATUS_MESSAGE, ResponseStatus.INVALID_AUTH_KEY.getStatusMsg());
					response.put(FieldKey.SK_STATUS_CODE, ResponseStatus.INVALID_AUTH_KEY.getStatusId());
					status = HttpStatus.UNAUTHORIZED;
				}
			} else {
				response.put(FieldKey.SK_STATUS_MESSAGE, ResponseStatus.INVALID_CLIENT_KEY.getStatusMsg() + " Empty");
				response.put(FieldKey.SK_STATUS_CODE, ResponseStatus.INVALID_CLIENT_KEY.getStatusId());
				status = HttpStatus.UNAUTHORIZED;
			}
		} catch (Exception e) {
			logger.debug(e.getMessage());
			status = HttpStatus.INTERNAL_SERVER_ERROR;
			response.put(FieldKey.SK_STATUS_CODE, ResponseStatus.FAILURE.getStatusId());
			response.put(FieldKey.SK_STATUS_MESSAGE, e.getMessage());
		}

		return new ResponseEntity<>(response, status);
	}


	@RequestMapping(value = "/subscribe/notify", method = RequestMethod.POST, produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<Map<String, Object>> subscribeNotify(@RequestHeader(value = "X-CLIENT-KEY") String clientKey,
														 @RequestHeader(value = "X-AUTH-KEY") String authKey, @RequestHeader(value = "X-CID") String custId,
														 @RequestBody LockerModel lockerModel) {

		HttpStatus status = HttpStatus.OK;
		Map<String, Object> response = new HashMap<>();
		try {
			if (clientKey != null && !clientKey.isEmpty()) {
				AuthDetail authDetail = configService.getAuthDetail(clientKey);
				if (authDetail == null) {
					response.put(FieldKey.SK_STATUS_MESSAGE, ResponseStatus.INVALID_CLIENT_KEY.getStatusMsg());
					response.put(FieldKey.SK_STATUS_CODE, ResponseStatus.INVALID_CLIENT_KEY.getStatusId());
					return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
				}
				if (authDetail.getAuth_key().equals(authKey)) {
					AES256Cipher cipher = configService.getAESForClientKeyMap(clientKey);
					try {
						Integer.valueOf(cipher.decrypt(custId));
						boolean isAuthorize = healthLockerService.subscribeNotify(lockerModel, "healthId@sbx");
						if (isAuthorize) {
							response.put(FieldKey.SK_STATUS_MESSAGE, ResponseStatus.SUCCESS.getStatusMsg());
							response.put(FieldKey.SK_STATUS_CODE, ResponseStatus.SUCCESS.getStatusId());
						} else {
							response.put(FieldKey.SK_STATUS_MESSAGE, ResponseStatus.FAILURE.getStatusMsg());
							response.put(FieldKey.SK_STATUS_CODE, ResponseStatus.FAILURE.getStatusId());
						}

					} catch (NumberFormatException exception) {
						response.put(FieldKey.SK_STATUS_MESSAGE, ResponseStatus.INVALID_FORMAT_PARAM.getStatusMsg()
								+ " Reason: customerId must be a number");
						response.put(FieldKey.SK_STATUS_CODE, ResponseStatus.FAILURE.getStatusId());
					}

				} else {
					response.put(FieldKey.SK_STATUS_MESSAGE, ResponseStatus.INVALID_AUTH_KEY.getStatusMsg());
					response.put(FieldKey.SK_STATUS_CODE, ResponseStatus.INVALID_AUTH_KEY.getStatusId());
					status = HttpStatus.UNAUTHORIZED;
				}
			} else {
				response.put(FieldKey.SK_STATUS_MESSAGE, ResponseStatus.INVALID_CLIENT_KEY.getStatusMsg() + " Empty");
				response.put(FieldKey.SK_STATUS_CODE, ResponseStatus.INVALID_CLIENT_KEY.getStatusId());
				status = HttpStatus.UNAUTHORIZED;
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
