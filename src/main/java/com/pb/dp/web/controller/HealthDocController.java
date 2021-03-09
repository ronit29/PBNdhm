package com.pb.dp.web.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pb.dp.model.HealthDoc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.pb.dp.enums.ResponseStatus;
import com.pb.dp.model.AuthDetail;
import com.pb.dp.model.CustHealthOtpRequest;
import com.pb.dp.model.FieldKey;
import com.pb.dp.service.ConfigService;
import com.pb.dp.service.HealthDocService;
import com.pb.dp.util.AES256Cipher;

/**
 * The Class HealthDocController.
 */
@RestController
@RequestMapping(value = "/doc")
public class HealthDocController {

	/** The logger. */
	private final Logger logger = LoggerFactory.getLogger(HealthDocController.class);

	/** The config service. */
	@Autowired
	private ConfigService configService;

	/** The health doc service. */
	@Autowired
	private HealthDocService healthDocService;

	/**
	 * Gets the document list.
	 *
	 * @param clientKey the client key
	 * @param authKey the auth key
	 * @param custId the cust id
	 * @return the document list
	 */
	@RequestMapping(value = "/getList", method = RequestMethod.GET, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<Map<String, Object>> getDocumentList(@RequestHeader(value = "X-CLIENT-KEY") String clientKey,
			@RequestHeader(value = "X-AUTH-KEY") String authKey, @RequestHeader(value = "X-CID") String custId) {
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
						List<Map<String, Object>> documentList = healthDocService.getDocumentList(customerId);
						response.put("data", documentList);
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
					status = HttpStatus.UNAUTHORIZED;
				}
			} else {
				response.put(FieldKey.SK_STATUS_MESSAGE, ResponseStatus.INVALID_CLIENT_KEY.getStatusMsg() + " Empty");
				response.put(FieldKey.SK_STATUS_CODE, ResponseStatus.INVALID_CLIENT_KEY.getStatusId());
				status = HttpStatus.UNAUTHORIZED;
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

	/**
	 * Doc upload.
	 *
	 * @param file the file
	 * @param payloadJSON the payload JSON
	 * @param clientKey the client key
	 * @param authKey the auth key
	 * @return the response entity
	 */
	@RequestMapping(value = "/uploadDocs", method = RequestMethod.POST, produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<Map<String, Object>> docUpload(@RequestParam(value = "file") MultipartFile file,
														@RequestParam(value = "payloadJSON") String payloadJSON,
														@RequestHeader(value = "X-CLIENT-KEY") String clientKey,
														@RequestHeader(value = "X-AUTH-KEY") String authKey,
														 @RequestHeader(value = "X-CID") String custId)
	{
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
						boolean documentUpload = healthDocService.docUpload(file, payloadJSON, customerId);
						response.put("isUploaded", documentUpload);
						if (documentUpload) {
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
	 * Doc update.
	 *
	 * @param id the Database id
	 * @param payloadJSON the payload JSON
	 * @param clientKey the client key
	 * @param authKey the auth key
	 * @param custId the cust id
	 * @return the response entity
	 */
	@RequestMapping(value = "/docUpdate", method = RequestMethod.POST, produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<Map<String, Object>> docUpdate(@RequestParam(value = "id") Integer id,
														@RequestParam(value = "payloadJSON") String payloadJSON,
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
						ObjectMapper mapper = new ObjectMapper();
						mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
						HealthDoc healthDoc = mapper.readValue(payloadJSON, HealthDoc.class);
						healthDoc.setCustomerId(customerId);
						boolean isValid = healthDocService.docUpdateValidate(id, healthDoc);
						if(!isValid){
							response.put(FieldKey.SK_STATUS_MESSAGE, ResponseStatus.INVALID_DOCUMENT.getStatusMsg());
							response.put(FieldKey.SK_STATUS_CODE, ResponseStatus.INVALID_DOCUMENT.getStatusId());
						}
						else {
							boolean documentUpdated = healthDocService.docUpdate(id, healthDoc);
							response.put("isUpdate", documentUpdated);
							if (documentUpdated) {
								response.put(FieldKey.SK_STATUS_MESSAGE, ResponseStatus.SUCCESS.getStatusMsg());
								response.put(FieldKey.SK_STATUS_CODE, ResponseStatus.SUCCESS.getStatusId());
							} else {
								response.put(FieldKey.SK_STATUS_MESSAGE, ResponseStatus.FAILURE.getStatusMsg());
								response.put(FieldKey.SK_STATUS_CODE, ResponseStatus.FAILURE.getStatusId());
							}
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
	 * Doc search.
	 *
	 * @param payloadJson the payload json
	 * @param clientKey the client key
	 * @param authKey the auth key
	 * @param custId the cust id
	 * @return the response entity
	 */
	@RequestMapping(value = "/docSearch", method = RequestMethod.POST, produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<Map<String, Object>> docSearch(@RequestBody Map<String, Object> payloadJson,
			@RequestHeader(value = "X-CLIENT-KEY") String clientKey,
			@RequestHeader(value = "X-AUTH-KEY") String authKey, @RequestHeader(value = "X-CID") String custId) {
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
						List<Map<String, Object>> documents = healthDocService.docSearch(payloadJson, customerId);
						response.put("data", documents);
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
	 * Doc delete.
	 *
	 * @param clientKey the client key
	 * @param authKey the auth key
	 * @param custId the cust id
	 * @return the response entity
	 */
	@RequestMapping(value = "/docDelete/{id}", method = RequestMethod.DELETE, produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<Map<String, Object>> docDelete(@PathVariable Integer id,
			@RequestHeader(value = "X-CLIENT-KEY") String clientKey,
			@RequestHeader(value = "X-AUTH-KEY") String authKey, @RequestHeader(value = "X-CID") String custId) {
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
						//Integer item = Integer.valueOf(cipher.decrypt(id));
						boolean delete = healthDocService.docDelete(id, customerId);
						response.put("isDeleted", delete);
						if (delete) {
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
	 * Gets the doc owners.
	 *
	 * @param clientKey the client key
	 * @param authKey the auth key
	 * @param custId the cust id
	 * @param custHealthOtpRequest the cust health otp request
	 * @return the doc owners
	 */
	@RequestMapping(value = "/getDocOwners", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<Map<String, Object>> getDocOwners(@RequestHeader(value = "X-CLIENT-KEY") String clientKey,
			@RequestHeader(value = "X-AUTH-KEY") String authKey, @RequestHeader(value = "X-CID") String custId) {
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
						List<Map<String,Object>> docOwners = healthDocService.getDocOwners(customerId);
						if(null!=docOwners && !docOwners.isEmpty()) {
							response.put("data", docOwners);
							response.put(FieldKey.SK_STATUS_MESSAGE, ResponseStatus.SUCCESS.getStatusMsg());
							response.put(FieldKey.SK_STATUS_CODE, ResponseStatus.SUCCESS.getStatusId());
						}else {
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
