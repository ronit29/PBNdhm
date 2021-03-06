package com.pb.dp.web.controller;

import com.pb.dp.enums.ResponseStatus;
import com.pb.dp.model.AuthDetail;
import com.pb.dp.model.FieldKey;
import com.pb.dp.model.PatientAuth;
import com.pb.dp.service.ConfigService;
import com.pb.dp.service.LockerAuthService;
import com.pb.dp.util.AES256Cipher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "/lockerAuth")
public class LockerAuthController {

    @Autowired
    ConfigService configService;

    @Autowired
    LockerAuthService lockerAuthService;

    private final Logger logger = LoggerFactory.getLogger(LockerAuthController.class);

    @RequestMapping(value = "/notify", method = RequestMethod.POST, produces = {
            MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Map<String, Object>> authNotify(@RequestHeader(value = "X-CLIENT-KEY") String clientKey,
                                                          @RequestHeader(value = "X-AUTH-KEY") String authKey,
                                                          @RequestHeader(value = "X-CID") String custId,
                                                          @RequestBody PatientAuth payload) {

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
                        boolean notify = lockerAuthService.userAuthNotify(payload);
                        if (notify) {
                            response.put(FieldKey.SK_STATUS_MESSAGE, com.pb.dp.enums.ResponseStatus.SUCCESS.getStatusMsg());
                            response.put(FieldKey.SK_STATUS_CODE, com.pb.dp.enums.ResponseStatus.SUCCESS.getStatusId());
                        } else {
                            response.put(FieldKey.SK_STATUS_MESSAGE, com.pb.dp.enums.ResponseStatus.FAILURE.getStatusMsg());
                            response.put(FieldKey.SK_STATUS_CODE, com.pb.dp.enums.ResponseStatus.FAILURE.getStatusId());
                        }
                    } catch (NumberFormatException exception) {
                        response.put(FieldKey.SK_STATUS_MESSAGE, ResponseStatus.INVALID_FORMAT_PARAM.getStatusMsg()
                                + " Reason: customerId must be a number");
                        response.put(FieldKey.SK_STATUS_CODE, ResponseStatus.FAILURE.getStatusId());
                    }
                } else {
                    response.put(FieldKey.SK_STATUS_MESSAGE, ResponseStatus.INVALID_AUTH_KEY.getStatusMsg());
                    response.put(FieldKey.SK_STATUS_CODE, ResponseStatus.INVALID_AUTH_KEY.getStatusId());
                    return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
                }
            } else {
                response.put(FieldKey.SK_STATUS_MESSAGE, ResponseStatus.INVALID_CLIENT_KEY.getStatusMsg() + " Empty");
                response.put(FieldKey.SK_STATUS_CODE, ResponseStatus.INVALID_CLIENT_KEY.getStatusId());
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            logger.debug(e.getMessage());
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            response.put(FieldKey.SK_STATUS_CODE, ResponseStatus.FAILURE.getStatusId());
            response.put(FieldKey.SK_STATUS_MESSAGE, e.getMessage());
        }

        return new ResponseEntity<>(response, status);
    }

    @RequestMapping(value = "/on-notify", method = RequestMethod.POST, produces = {
            MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Map<String, Object>> authNotifyCallBack(@RequestHeader(value = "X-CLIENT-KEY") String clientKey,
                                                                  @RequestHeader(value = "X-AUTH-KEY") String authKey,
                                                                  @RequestHeader(value = "X-CID") String custId,
                                                                  @RequestBody Map<String, Object> payload) {

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
                        boolean notify = lockerAuthService.authNotifyCallBack(payload);
                        if (notify) {
                            response.put(FieldKey.SK_STATUS_MESSAGE, com.pb.dp.enums.ResponseStatus.SUCCESS.getStatusMsg());
                            response.put(FieldKey.SK_STATUS_CODE, com.pb.dp.enums.ResponseStatus.SUCCESS.getStatusId());
                        } else {
                            response.put(FieldKey.SK_STATUS_MESSAGE, com.pb.dp.enums.ResponseStatus.FAILURE.getStatusMsg());
                            response.put(FieldKey.SK_STATUS_CODE, com.pb.dp.enums.ResponseStatus.FAILURE.getStatusId());
                        }
                    } catch (NumberFormatException exception) {
                        response.put(FieldKey.SK_STATUS_MESSAGE, ResponseStatus.INVALID_FORMAT_PARAM.getStatusMsg()
                                + " Reason: customerId must be a number");
                        response.put(FieldKey.SK_STATUS_CODE, ResponseStatus.FAILURE.getStatusId());
                    }
                } else {
                    response.put(FieldKey.SK_STATUS_MESSAGE, ResponseStatus.INVALID_AUTH_KEY.getStatusMsg());
                    response.put(FieldKey.SK_STATUS_CODE, ResponseStatus.INVALID_AUTH_KEY.getStatusId());
                    return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
                }
            } else {
                response.put(FieldKey.SK_STATUS_MESSAGE, ResponseStatus.INVALID_CLIENT_KEY.getStatusMsg() + " Empty");
                response.put(FieldKey.SK_STATUS_CODE, ResponseStatus.INVALID_CLIENT_KEY.getStatusId());
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
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
