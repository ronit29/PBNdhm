package com.pb.dp.web.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
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
import com.pb.dp.model.CustHealthOtpRequest;
import com.pb.dp.model.CustomerHealth;
import com.pb.dp.model.FieldKey;
import com.pb.dp.model.GetHealthProfileRequest;
import com.pb.dp.service.ConfigService;
import com.pb.dp.service.HealthService;
import com.pb.dp.util.AES256Cipher;

@RestController
@RequestMapping(value = "customer")
public class HealthController {

	private final Logger logger = LoggerFactory.getLogger(HealthController.class);

	@Autowired
	private ConfigService configService;
	
	@Autowired
	private HealthService healthService;

	@RequestMapping(value = "/getCustHealthDetails", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<Map<String, Object>> getCustHealthDetails(@RequestHeader(value = "X-CLIENT-KEY") String clientKey, 
			@RequestHeader(value = "X-AUTH-KEY") String authKey,
			@RequestHeader(value = "X-CID") String custId, 
			@RequestBody CustHealthOtpRequest custHealthOtpRequest) {
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
						int customerId = Integer.valueOf(cipher.decrypt(custId));
						List<CustomerHealth> responseForHealth = healthService.getCustHealthDetails(custHealthOtpRequest.getMobileNo(),customerId);
						response.put("data", responseForHealth);
						response.put(FieldKey.SK_STATUS_MESSAGE, ResponseStatus.SUCCESS.getStatusMsg());
						response.put(FieldKey.SK_STATUS_CODE, ResponseStatus.SUCCESS.getStatusId());
						
					} catch (NumberFormatException exception) {
						response.put(FieldKey.SK_STATUS_MESSAGE, ResponseStatus.INVALID_FORMAT_PARAM.getStatusMsg()
								+ " Reason: customerId must be a number");
						response.put(FieldKey.SK_STATUS_CODE, ResponseStatus.FAILURE.getStatusId());
					}

				} else {
					response.put(FieldKey.SK_STATUS_MESSAGE, ResponseStatus.INVALID_AUTH_KEY.getStatusMsg());
					response.put(FieldKey.SK_STATUS_CODE, ResponseStatus.INVALID_AUTH_KEY.getStatusId());
					status =  HttpStatus.UNAUTHORIZED;
				}
			} else {
				response.put(FieldKey.SK_STATUS_MESSAGE, ResponseStatus.INVALID_CLIENT_KEY.getStatusMsg() + " Empty");
				response.put(FieldKey.SK_STATUS_CODE, ResponseStatus.INVALID_CLIENT_KEY.getStatusId());
				status =  HttpStatus.UNAUTHORIZED;
			}
		} catch (Exception e) {
			logger.debug(e.getMessage());
			status = HttpStatus.INTERNAL_SERVER_ERROR;
			response.put(FieldKey.SK_STATUS_CODE, ResponseStatus.FAILURE.getStatusId());
			response.put(FieldKey.SK_STATUS_MESSAGE, e.getMessage());
		}

		return new ResponseEntity<>(response, status);
	}
	
	@RequestMapping(value = "/getHealthProfile", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<Map<String, Object>> getHealthProfile(@RequestHeader(value = "X-CLIENT-KEY") String clientKey, 
			@RequestHeader(value = "X-AUTH-KEY") String authKey,
			@RequestHeader(value = "X-CID") String custId, 
			@RequestBody GetHealthProfileRequest custHealthOtpRequest) {
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
						int customerId = Integer.valueOf(cipher.decrypt(custId));
						CustomerHealth responseForHealth = healthService.getHealthProfile(customerId, custHealthOtpRequest);
						if(Objects.nonNull(responseForHealth)){
						response.put("data", responseForHealth);
						if (null == responseForHealth.getTxnId()) {
							response.put(FieldKey.SK_STATUS_MESSAGE, ResponseStatus.SUCCESS.getStatusMsg());
							response.put(FieldKey.SK_STATUS_CODE, ResponseStatus.SUCCESS.getStatusId());
						} else if (responseForHealth.getTxnId().equals(StringUtils.EMPTY)) {
							response.put(FieldKey.SK_STATUS_MESSAGE, ResponseStatus.AUTH_INIT_FAILED.getStatusMsg());
							response.put(FieldKey.SK_STATUS_CODE, ResponseStatus.AUTH_INIT_FAILED.getStatusId());
						} else {
							response.put(FieldKey.SK_STATUS_MESSAGE, ResponseStatus.INVALID_XTOKEN.getStatusMsg());
							response.put(FieldKey.SK_STATUS_CODE, ResponseStatus.INVALID_XTOKEN.getStatusId());
						}
					} else{
							response.put(FieldKey.SK_STATUS_MESSAGE, ResponseStatus.NO_RECORD_FOUND.getStatusMsg());
							response.put(FieldKey.SK_STATUS_CODE, ResponseStatus.NO_RECORD_FOUND.getStatusId());
						}
					} catch (NumberFormatException exception) {
						response.put(FieldKey.SK_STATUS_MESSAGE, ResponseStatus.INVALID_FORMAT_PARAM.getStatusMsg()
								+ " Reason: customerId must be a number");
						response.put(FieldKey.SK_STATUS_CODE, ResponseStatus.FAILURE.getStatusId());
					}

				} else {
					response.put(FieldKey.SK_STATUS_MESSAGE, ResponseStatus.INVALID_AUTH_KEY.getStatusMsg());
					response.put(FieldKey.SK_STATUS_CODE, ResponseStatus.INVALID_AUTH_KEY.getStatusId());
					status =  HttpStatus.UNAUTHORIZED;
				}
			} else {
				response.put(FieldKey.SK_STATUS_MESSAGE, ResponseStatus.INVALID_CLIENT_KEY.getStatusMsg() + " Empty");
				response.put(FieldKey.SK_STATUS_CODE, ResponseStatus.INVALID_CLIENT_KEY.getStatusId());
				status =  HttpStatus.UNAUTHORIZED;
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.debug(e.getMessage());
			status = HttpStatus.INTERNAL_SERVER_ERROR;
			response.put(FieldKey.SK_STATUS_CODE, ResponseStatus.FAILURE.getStatusId());
			response.put(FieldKey.SK_STATUS_MESSAGE, e.getMessage());
		}

		return new ResponseEntity<>(response, status);
	}
	
	
	@RequestMapping(value = "/getCardContent", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<Map<String, Object>> getCardContent(@RequestHeader(value = "X-CLIENT-KEY") String clientKey, 
			@RequestHeader(value = "X-AUTH-KEY") String authKey,
			@RequestHeader(value = "X-CID") String custId, 
			@RequestBody GetHealthProfileRequest custHealthOtpRequest) {
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
						int customerId = Integer.valueOf(cipher.decrypt(custId));
						String responseForCard = healthService.getCardContent(customerId,custHealthOtpRequest);
						if(null!=responseForCard && !responseForCard.isEmpty()) {
							response.put("data", responseForCard);
							response.put(FieldKey.SK_STATUS_MESSAGE, ResponseStatus.SUCCESS.getStatusMsg());
							response.put(FieldKey.SK_STATUS_CODE, ResponseStatus.SUCCESS.getStatusId());
						}else {
							response.put("data", responseForCard);
							response.put(FieldKey.SK_STATUS_MESSAGE, ResponseStatus.FAILURE.getStatusMsg());
							response.put(FieldKey.SK_STATUS_CODE, ResponseStatus.FAILURE.getStatusId());
						}
						
					} catch (NumberFormatException exception) {
						response.put(FieldKey.SK_STATUS_MESSAGE, ResponseStatus.INVALID_FORMAT_PARAM.getStatusMsg()
								+ " Reason: customerId must be a number");
						response.put(FieldKey.SK_STATUS_CODE, ResponseStatus.INVALID_FORMAT_PARAM.getStatusId());
					}

				} else {
					response.put(FieldKey.SK_STATUS_MESSAGE, ResponseStatus.INVALID_AUTH_KEY.getStatusMsg());
					response.put(FieldKey.SK_STATUS_CODE, ResponseStatus.INVALID_AUTH_KEY.getStatusId());
					status =  HttpStatus.UNAUTHORIZED;
				}
			} else {
				response.put(FieldKey.SK_STATUS_MESSAGE, ResponseStatus.INVALID_CLIENT_KEY.getStatusMsg() + " Empty");
				response.put(FieldKey.SK_STATUS_CODE, ResponseStatus.INVALID_CLIENT_KEY.getStatusId());
				status =  HttpStatus.UNAUTHORIZED;
			}
		} catch (Exception e) {
			logger.debug(e.getMessage());
			status = HttpStatus.INTERNAL_SERVER_ERROR;
			response.put(FieldKey.SK_STATUS_CODE, ResponseStatus.FAILURE.getStatusId());
			response.put(FieldKey.SK_STATUS_MESSAGE, e.getMessage());
		}

		return new ResponseEntity<>(response, status);
	}
	
	@RequestMapping(value = "/getCustomerProfile", method = RequestMethod.GET, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<Map<String, Object>> getCustomerProfile(@RequestHeader(value = "X-CLIENT-KEY") String clientKey, 
			@RequestHeader(value = "X-AUTH-KEY") String authKey,
			@RequestHeader(value = "X-CID") String custId) {
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
						int customerId = Integer.valueOf(cipher.decrypt(custId));
						Map<String, Object> responseMap = healthService.getCustomerProfile(customerId);
					if(!responseMap.isEmpty()) {
						response.put("data",responseMap);
						response.put(FieldKey.SK_STATUS_MESSAGE, ResponseStatus.SUCCESS.getStatusMsg());
						response.put(FieldKey.SK_STATUS_CODE, ResponseStatus.SUCCESS.getStatusId());
					} else{
							response.put(FieldKey.SK_STATUS_MESSAGE, ResponseStatus.NO_RECORD_FOUND.getStatusMsg());
							response.put(FieldKey.SK_STATUS_CODE, ResponseStatus.NO_RECORD_FOUND.getStatusId());
						}
					} catch (NumberFormatException exception) {
						response.put(FieldKey.SK_STATUS_MESSAGE, ResponseStatus.INVALID_FORMAT_PARAM.getStatusMsg()
								+ " Reason: customerId must be a number");
						response.put(FieldKey.SK_STATUS_CODE, ResponseStatus.FAILURE.getStatusId());
					}

				} else {
					response.put(FieldKey.SK_STATUS_MESSAGE, ResponseStatus.INVALID_AUTH_KEY.getStatusMsg());
					response.put(FieldKey.SK_STATUS_CODE, ResponseStatus.INVALID_AUTH_KEY.getStatusId());
					status =  HttpStatus.UNAUTHORIZED;
				}
			} else {
				response.put(FieldKey.SK_STATUS_MESSAGE, ResponseStatus.INVALID_CLIENT_KEY.getStatusMsg() + " Empty");
				response.put(FieldKey.SK_STATUS_CODE, ResponseStatus.INVALID_CLIENT_KEY.getStatusId());
				status =  HttpStatus.UNAUTHORIZED;
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
