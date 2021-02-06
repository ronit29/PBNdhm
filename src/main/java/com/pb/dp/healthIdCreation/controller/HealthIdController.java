package com.pb.dp.healthIdCreation.controller;

import com.pb.dp.healthIdCreation.enums.NdhmVerifyOperation;
import com.pb.dp.healthIdCreation.model.CustomerDetails;
import com.pb.dp.healthIdCreation.model.NdhmMobOtpRequest;
import com.pb.dp.healthIdCreation.service.HealthIdService;
import com.pb.dp.model.AuthDetail;
import com.pb.dp.model.FieldKey;
import com.pb.dp.service.ConfigService;
import com.pb.dp.enums.ResponseStatus;

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
@RequestMapping(value = "/healthId")
public class HealthIdController {

   @Autowired
   ConfigService configService;

   @Autowired
   HealthIdService healthIdService;

   private static final Logger logger = LoggerFactory.getLogger(HealthIdController.class);

   @RequestMapping(value = "/register/mobile", method = RequestMethod.POST, produces = { MediaType.APPLICATION_JSON_VALUE })
   public ResponseEntity<Map<String, Object>> registerViaMobile(@RequestBody CustomerDetails customerDetail,
                                                                @RequestHeader(value = "X-CLIENT-KEY") String clientKey,
                                                                @RequestHeader(value = "X-AUTH-KEY") String authKey,
                                                                @RequestHeader(value = "X-CID") String custId){

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
                  response = this.healthIdService.registerViaMobile(customerDetail,customerId);
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

   @RequestMapping(value = "/verifyOtp/mobile", method = RequestMethod.POST, produces = { MediaType.APPLICATION_JSON_VALUE })
   public ResponseEntity<Map<String, Object>> verifyViaMobile(@RequestBody NdhmMobOtpRequest ndhmMobOtpRequest,
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
                     if(ndhmMobOtpRequest.getOperation().equals(NdhmVerifyOperation.REGISTER.getOperationId()))
                        response = this.healthIdService.verifyForRegistration(ndhmMobOtpRequest, customerId);
                     else if(ndhmMobOtpRequest.getOperation().equals(NdhmVerifyOperation.UPDATE_PROFILE  .getOperationId()))
                        response = this.healthIdService.updateHealthIdProfile(ndhmMobOtpRequest,customerId);

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

   @RequestMapping(value = "/resendOtp/mobile", method = RequestMethod.POST, produces = { MediaType.APPLICATION_JSON_VALUE })
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
         logger.debug(e.getMessage());
         e.printStackTrace();
         status = HttpStatus.INTERNAL_SERVER_ERROR;
         response.put(FieldKey.SK_STATUS_CODE, ResponseStatus.FAILURE.getStatusId());
         response.put(FieldKey.SK_STATUS_MESSAGE, e.getMessage());
      }

      return new ResponseEntity<>(response, status);

   }


   @RequestMapping(value = "/updateProfile", method = RequestMethod.POST, produces = { MediaType.APPLICATION_JSON_VALUE })
   public ResponseEntity<Map<String, Object>> updateHealthIdProfile(@RequestBody CustomerDetails customerDetails,
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
                  response = this.healthIdService.generateOtpForUpdate(customerDetails,customerId);
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


}
