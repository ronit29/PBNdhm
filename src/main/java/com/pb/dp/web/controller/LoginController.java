package com.pb.dp.web.controller;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pb.dp.enums.ResponseStatus;
import com.pb.dp.model.AuthDetail;
import com.pb.dp.model.FieldKey;
import com.pb.dp.service.ConfigService;
import com.pb.dp.service.LoginService;
import com.pb.dp.util.AES256Cipher;

@RestController
@RequestMapping(value = "/login")
public class LoginController {
    private final Logger logger = LoggerFactory.getLogger(LoginController.class);
    @Autowired
    private ConfigService configService;
//    @Autowired
//    private OtpValidator otpValidator;
    @Autowired
    private LoginService loginService;

    @RequestMapping(value = "/sendOtp", method = RequestMethod.POST, produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<Map<String, Object>> sendOtp(@RequestHeader(value = "X-CLIENT-KEY") String clientKey,
                                                       @RequestHeader(value = "X-AUTH-KEY") String authKey,
                                                       @RequestParam(required = true) String mobileNo) {
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
                        Long mobile = Long.getLong(cipher.decrypt(mobileNo));
                        response = loginService.sendOtp(mobile);
                    }catch (NumberFormatException exception){
                        response.put(FieldKey.SK_STATUS_MESSAGE, ResponseStatus.INVALID_FORMAT_PARAM.getStatusMsg() + " Reason: mobileNo must be a number");
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
