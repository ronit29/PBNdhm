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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pb.dp.enums.ResponseStatus;
import com.pb.dp.model.AuthDetail;
import com.pb.dp.model.CustHealthOtpRequest;
import com.pb.dp.model.FieldKey;
import com.pb.dp.service.ConfigService;
import com.pb.dp.service.OtpService;
import com.pb.dp.util.AES256Cipher;

@RestController
@RequestMapping(value = "otp")
public class OtpController {
	private final Logger logger = LoggerFactory.getLogger(OtpController.class);
	@Autowired
	private ConfigService configService;
	
	@Autowired
	private OtpService otpService;

	@RequestMapping(value = "/verifyotp", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<Map<String, Object>> verifyotp(@RequestHeader(value = "X-CLIENT-KEY") String clientKey, 
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
						boolean isOtpverified = otpService.isVerified(custHealthOtpRequest.getOtp(),custHealthOtpRequest.getMobileNo(),customerId);
						response.put("customerId", custId);
						response.put("isOtpVerified", isOtpverified);
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

}
