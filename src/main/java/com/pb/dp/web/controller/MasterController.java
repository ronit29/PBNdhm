package com.pb.dp.web.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pb.dp.model.AuthDetail;
import com.pb.dp.service.ConfigService;
import com.pb.dp.util.AES256Cipher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.pb.dp.enums.ResponseStatus;
import com.pb.dp.model.FieldKey;
import com.pb.dp.service.MasterService;

/**
 * This controller is used to provide master data
 * 
 * @author Aditya Rathore
 *
 */
@RestController
@RequestMapping(value = "master")
public class MasterController {

	 private final Logger logger = LoggerFactory.getLogger(MasterController.class);
	 
	 @Autowired
	 private MasterService masterService;

	 @Autowired
	 private ConfigService configService;
	 /**
	  * This method is used to get list of states
	  * 
	 * @return
	 */
	@RequestMapping(value = "/state",method = RequestMethod.GET,produces = {MediaType.APPLICATION_JSON_VALUE})
	 public ResponseEntity<Map<String,Object>> getState(){
		 Map<String,Object> response = new HashMap<>();
		 HttpStatus status = HttpStatus.OK;
		 try {
			 List<Map<String,Object>> result = masterService.getState();
			 if(result!=null && !result.isEmpty()) {
				 response.put("data",result);
				 response.put(FieldKey.SK_STATUS_CODE,ResponseStatus.SUCCESS.getStatusId());
				 response.put(FieldKey.SK_STATUS_MESSAGE,ResponseStatus.SUCCESS.getStatusMsg());
			 } else {
				 response.put(FieldKey.SK_STATUS_CODE,ResponseStatus.NO_RECORD_FOUND.getStatusId());
				 response.put(FieldKey.SK_STATUS_MESSAGE,ResponseStatus.NO_RECORD_FOUND.getStatusMsg());
			 }
			 
		 } catch(Exception e) {
			 logger.error("Exception caught in getState method: {}", e.getMessage(),e);
			 response.put(FieldKey.SK_STATUS_CODE,ResponseStatus.FAILURE.getStatusId());
			 response.put(FieldKey.SK_STATUS_MESSAGE,ResponseStatus.FAILURE.getStatusMsg());
		 }
		 return new ResponseEntity<>(response,status);
		 
	 }
	 
	 /**
	  * This method is used to get all districts for given stateCode
	  * 
	 * @param stateCode
	 * @return
	 */
	@RequestMapping(value = "/district",method = RequestMethod.GET,produces = {MediaType.APPLICATION_JSON_VALUE})
	 public ResponseEntity<Map<String,Object>> getDistrictsForState(@RequestParam Integer stateCode){
		 Map<String,Object> response = new HashMap<>();
		 HttpStatus status = HttpStatus.OK;
		 try {
			 List<Map<String,Object>> result = masterService.getDistrictsForState(stateCode);
			 response.put("stateId",stateCode);
			 if(result!=null && !result.isEmpty()) {
				 response.put("data",result);
				 response.put(FieldKey.SK_STATUS_CODE,ResponseStatus.SUCCESS.getStatusId());
				 response.put(FieldKey.SK_STATUS_MESSAGE,ResponseStatus.SUCCESS.getStatusMsg());
			 } else {
				 response.put(FieldKey.SK_STATUS_CODE,ResponseStatus.NO_RECORD_FOUND.getStatusId());
				 response.put(FieldKey.SK_STATUS_MESSAGE,ResponseStatus.NO_RECORD_FOUND.getStatusMsg());
			 }
			 
		 } catch(Exception e) {
			 logger.error("Exception caught in getDistrictsForState method: {}", e.getMessage(),e);
			 response.put(FieldKey.SK_STATUS_CODE,ResponseStatus.FAILURE.getStatusId());
			 response.put(FieldKey.SK_STATUS_MESSAGE,ResponseStatus.FAILURE.getStatusMsg());
		 }
		 return new ResponseEntity<>(response,status);
		 
	 }
	
	/**
	  * This method is used to get list of relations
	  * 
	 * @return <code>List<Map<Strig, Object>></code> of relations
	 */
	@RequestMapping(value = "/relation/list",method = RequestMethod.GET,produces = {MediaType.APPLICATION_JSON_VALUE})
	 public ResponseEntity<Map<String,Object>> getRelation( @RequestHeader(value = "X-CLIENT-KEY") String clientKey,
															@RequestHeader(value = "X-AUTH-KEY") String authKey,
															@RequestHeader(value = "X-CID") String custId){
		 Map<String,Object> response = new HashMap<>();
		 HttpStatus status = HttpStatus.OK;
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
						 List<Map<String,Object>> result = masterService.getRelations(customerId);
						 if(result!=null && !result.isEmpty()) {
							 response.put("data",result);
							 response.put(FieldKey.SK_STATUS_CODE,ResponseStatus.SUCCESS.getStatusId());
							 response.put(FieldKey.SK_STATUS_MESSAGE,ResponseStatus.SUCCESS.getStatusMsg());
						 } else {
							 response.put(FieldKey.SK_STATUS_CODE,ResponseStatus.NO_RECORD_FOUND.getStatusId());
							 response.put(FieldKey.SK_STATUS_MESSAGE,ResponseStatus.NO_RECORD_FOUND.getStatusMsg());
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
		 } catch(Exception e) {
			 logger.error("Exception caught in getState method: {} ", e.getMessage(),e);
			 response.put(FieldKey.SK_STATUS_CODE,ResponseStatus.FAILURE.getStatusId());
			 response.put(FieldKey.SK_STATUS_MESSAGE,ResponseStatus.FAILURE.getStatusMsg());
		 }
		 return new ResponseEntity<>(response,status);
		 
	 }
	
	@RequestMapping(value = "/docType",method = RequestMethod.GET,produces = {MediaType.APPLICATION_JSON_VALUE})
	 public ResponseEntity<Map<String,Object>> getdocType(){
		 Map<String,Object> response = new HashMap<>();
		 HttpStatus status = HttpStatus.OK;
		 try {
			 List<Map<String,Object>> result = masterService.getdocType();
			 if(result!=null && !result.isEmpty()) {
				 response.put("data",result);
				 response.put(FieldKey.SK_STATUS_CODE,ResponseStatus.SUCCESS.getStatusId());
				 response.put(FieldKey.SK_STATUS_MESSAGE,ResponseStatus.SUCCESS.getStatusMsg());
			 } else {
				 response.put(FieldKey.SK_STATUS_CODE,ResponseStatus.NO_RECORD_FOUND.getStatusId());
				 response.put(FieldKey.SK_STATUS_MESSAGE,ResponseStatus.NO_RECORD_FOUND.getStatusMsg());
			 }
			 
		 } catch(Exception e) {
			 logger.error("Exception caught in getState method: {}", e.getMessage(),e);
			 response.put(FieldKey.SK_STATUS_CODE,ResponseStatus.FAILURE.getStatusId());
			 response.put(FieldKey.SK_STATUS_MESSAGE,ResponseStatus.FAILURE.getStatusMsg());
		 }
		 return new ResponseEntity<>(response,status);
		 
	 }
	
}
