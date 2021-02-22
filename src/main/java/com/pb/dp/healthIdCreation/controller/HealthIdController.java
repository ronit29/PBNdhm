package com.pb.dp.healthIdCreation.controller;

import com.pb.dp.healthIdCreation.enums.NdhmVerifyOperation;
import com.pb.dp.healthIdCreation.model.*;
import com.pb.dp.healthIdCreation.service.HealthIdService;
import com.pb.dp.model.AuthDetail;
import com.pb.dp.model.FieldKey;
import com.pb.dp.model.GetHealthProfileRequest;
import com.pb.dp.service.ConfigService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pb.dp.enums.ResponseStatus;

import com.pb.dp.util.AES256Cipher;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping(value = "/healthId")
public class HealthIdController {

    @Autowired
    ConfigService configService;

    @Autowired
    HealthIdService healthIdService;

    private static final Logger logger = LoggerFactory.getLogger(HealthIdController.class);

    private static Map<Integer, CustomerDetails> profileData = new HashMap<>();

    @RequestMapping(value = "/register/mobile", method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Map<String, Object>> registerViaMobile(@RequestBody CustomerDetails customerDetail,
                                                                 @RequestHeader(value = "X-CLIENT-KEY") String clientKey,
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
                        Map<Integer, CustomerDetails> registerProfileData = new HashMap<>();
                        profileData.put(customerId, customerDetail);
                        response = this.healthIdService.registerViaMobile(customerDetail, customerId);
                        logger.info("[registerViaMobile] profile data:: {}", profileData.get(customerId));
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
            e.printStackTrace();
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            response.put(FieldKey.SK_STATUS_CODE, ResponseStatus.FAILURE.getStatusId());
            response.put(FieldKey.SK_STATUS_MESSAGE, e.getMessage());
        }
        return new ResponseEntity<>(response, status);
    }

    @RequestMapping(value = "/verifyOtp/mobile", method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Map<String, Object>> verifyViaMobile(@RequestBody NdhmMobOtpRequest ndhmMobOtpRequest,
                                                               @RequestHeader(value = "X-CLIENT-KEY") String clientKey,
                                                               @RequestHeader(value = "X-AUTH-KEY") String authKey,
                                                               @RequestHeader(value = "X-CID") String custId, HttpSession httpSession) throws Exception {

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
                        logger.info("[verifyViaMobile] profile data:: {}", profileData.get(customerId));
                        CustomerDetails customerProfileData = profileData.get(customerId);
                        if (ObjectUtils.isNotEmpty(customerProfileData)) {
                            if (Objects.isNull(ndhmMobOtpRequest.getOperation())) {
                                response = this.healthIdService.verifyForRegistration(ndhmMobOtpRequest, customerProfileData, customerId);
                            } else if (ndhmMobOtpRequest.getOperation().equals(NdhmVerifyOperation.REGISTER.getOperationId())) {
                                response = this.healthIdService.verifyForRegistration(ndhmMobOtpRequest, customerProfileData, customerId);
                                if (ObjectUtils.isNotEmpty(response.get("verify")) && response.get("verify").equals(true)) {
                                    profileData.replace(customerId, customerProfileData, null);
                                }
                            } else if (ndhmMobOtpRequest.getOperation().equals(NdhmVerifyOperation.UPDATE_PROFILE.getOperationId())) {
                                response = this.healthIdService.updateHealthIdProfile(ndhmMobOtpRequest, customerProfileData, customerId);
                            } else if (ndhmMobOtpRequest.getOperation().equals(NdhmVerifyOperation.DELETE_PROFILE.getOperationId())) {
                                response = this.healthIdService.deleteHealthId(ndhmMobOtpRequest);
                            }
                        } else {
                            response.put(FieldKey.SK_STATUS_MESSAGE, ResponseStatus.PROFILE_DATA_MISSING.getStatusMsg());
                            response.put(FieldKey.SK_STATUS_CODE, ResponseStatus.PROFILE_DATA_MISSING.getStatusId());
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
            logger.error(e.getMessage());
            e.printStackTrace();
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            response.put(FieldKey.SK_STATUS_CODE, ResponseStatus.FAILURE.getStatusId());
            response.put(FieldKey.SK_STATUS_MESSAGE, e.getMessage());
        }
        return new ResponseEntity<>(response, status);
    }

    @RequestMapping(value = "/resendOtp/mobile", method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Map<String, Object>> resendNDHMMobileOTP(@RequestBody NdhmMobOtpRequest ndhmMobOtpRequest,
                                                                   @RequestHeader(value = "X-CLIENT-KEY") String clientKey,
                                                                   @RequestHeader(value = "X-AUTH-KEY") String authKey,
                                                                   @RequestHeader(value = "X-CID") String custId) throws Exception {

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
                        response = this.healthIdService.resendNdhmOtp(ndhmMobOtpRequest.getTxnId());
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
            logger.error(e.getMessage());
            e.printStackTrace();
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            response.put(FieldKey.SK_STATUS_CODE, ResponseStatus.FAILURE.getStatusId());
            response.put(FieldKey.SK_STATUS_MESSAGE, e.getMessage());
        }
        return new ResponseEntity<>(response, status);

    }


    @RequestMapping(value = "/updateProfile", method = RequestMethod.POST, produces = {
            MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Map<String, Object>> updateHealthIdProfile(@RequestParam(value = "file") MultipartFile file,
                                                                     @RequestParam(value = "payloadJSON") String payloadJSON,
                                                                     @RequestHeader(value = "X-CLIENT-KEY") String clientKey,
                                                                     @RequestHeader(value = "X-AUTH-KEY") String authKey, @RequestHeader(value = "X-CID") String custId) {
        HttpStatus status = HttpStatus.OK;
        Map<String, Object> response = new HashMap<>();
        try {
            CustomerDetails customerDetails = new CustomerDetails();
            ObjectMapper mapper = new ObjectMapper();
            customerDetails = mapper.readValue(payloadJSON, CustomerDetails.class);
            customerDetails.setProfilePhoto(Base64.getEncoder().encodeToString(file.getBytes()));
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
                        Map<Integer, CustomerDetails> registerProfileData = null;
                        if (ObjectUtils.isNotEmpty(profileData.get(customerId))) {
                            CustomerDetails customerProfileData = profileData.get(customerId);
                            if (ObjectUtils.isNotEmpty(customerProfileData)) {
                                profileData.replace(customerId, customerProfileData, customerDetails);
                            } else {
                                profileData.put(customerId, customerDetails);
                            }
                        } else {
                            profileData.put(customerId, customerDetails);
                        }
                        logger.info("[updateHealthIdProfile] profile data:: {}", profileData.get(customerId));
                        response = this.healthIdService.generateOtpForUpdate(customerDetails, customerId);
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
            logger.error(e.getMessage());
            e.printStackTrace();
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            response.put(FieldKey.SK_STATUS_CODE, ResponseStatus.FAILURE.getStatusId());
            response.put(FieldKey.SK_STATUS_MESSAGE, e.getMessage());
        }

        return new ResponseEntity<>(response, status);

    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Map<String, Object>> deleteHealthId(@RequestBody GetHealthProfileRequest getHealthProfileRequest,
                                                              @RequestHeader(value = "X-CLIENT-KEY") String clientKey,
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
                    response = healthIdService.deleteHealthId(getHealthProfileRequest.getHealthId());
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
            logger.error(e.getMessage());
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            response.put(FieldKey.SK_STATUS_CODE, ResponseStatus.FAILURE.getStatusId());
            response.put(FieldKey.SK_STATUS_MESSAGE, e.getMessage());
        }

        return new ResponseEntity<>(response, status);

    }

    @RequestMapping(value = "/register/aadhar", method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Map<String, Object>> registerWithAadhar(@RequestBody RegisterAadharRequest registerAadharRequest,
                                                                  @RequestHeader(value = "X-CLIENT-KEY") String clientKey,
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
                    response = healthIdService.registerWithAadhar(registerAadharRequest);
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
            logger.error(e.getMessage());
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            response.put(FieldKey.SK_STATUS_CODE, ResponseStatus.FAILURE.getStatusId());
            response.put(FieldKey.SK_STATUS_MESSAGE, e.getMessage());
        }

        return new ResponseEntity<>(response, status);

    }

    @RequestMapping(value = "/register/verifyOtpWithAadhar", method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Map<String, Object>> verifyOtpWithAadhar(@RequestBody VerifyOtpWithAadharRequest verifyOtpWithAadharRequest,
                                                                   @RequestHeader(value = "X-CLIENT-KEY") String clientKey,
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
                    response = healthIdService.verifyOtpWithAadhar(verifyOtpWithAadharRequest);
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
            logger.error(e.getMessage());
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            response.put(FieldKey.SK_STATUS_CODE, ResponseStatus.FAILURE.getStatusId());
            response.put(FieldKey.SK_STATUS_MESSAGE, e.getMessage());
        }

        return new ResponseEntity<>(response, status);

    }

}
