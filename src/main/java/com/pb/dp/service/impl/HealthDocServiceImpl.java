package com.pb.dp.service.impl;

import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pb.dp.enums.ResponseStatus;
import com.pb.dp.model.CustomerDetails;
import com.pb.dp.model.FieldKey;
import com.pb.dp.model.HealthDoc;
import com.pb.dp.model.HealthId;
import com.pb.dp.util.S3Util;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.pb.dp.dao.HealthDocDao;
import com.pb.dp.service.HealthDocService;


/**
 * The Class HealthDocServiceImpl.
 */
@Service
public class HealthDocServiceImpl implements HealthDocService {

	/** The health doc dao. */
	@Autowired
	private HealthDocDao healthDocDao;
	
	/**
	 * Gets the document list.
	 *
	 * @param payloadJson the payload json
	 * @param customerId the customer id
	 * @return the document list
	 */
	@Override
	public List<Map<String, Object>> getDocumentList(Map<String, Object> payloadJson, int customerId) {
		return null;
	}

	/**
	 * Doc upload.
	 *
	 * @param file the file
	 * @param payloadJSON the payload JSON
	 * @param customerId the customer id
	 * @return true, if successful
	 */
	@Override
	public boolean docUpload(MultipartFile file, String payloadJSON, int customerId) throws Exception{

		HealthDoc healthDoc = new HealthDoc();
		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);

		healthDoc = mapper.readValue(payloadJSON, HealthDoc.class);
		healthDoc.setCustomerId(customerId);
		/**upload to S3*/
//		S3Util.postRequestMultiPart(file, payloadJSON);
		boolean uploaded = this.healthDocDao.uploadDocs(healthDoc, customerId);
		return uploaded;
	}

	/**
	 * Doc update.
	 *
	 * @param file the file
	 * @param payloadJSON the payload JSON
	 * @param customerId the customer id
	 * @return true, if successful
	 */
	@Override
	public boolean docUpdate(MultipartFile file, String payloadJSON, int customerId) {
		return false;
	}

	/**
	 * Doc search.
	 *
	 * @param payloadJson the payload json
	 * @param customerId the customer id
	 * @return the list
	 */
	@Override
	public List<Map<String, Object>> docSearch(Map<String, Object> payloadJson, int customerId) {
		return null;
	}

	/**
	 * Doc delete.
	 *
	 * @param payloadJson the payload json
	 * @param customerId the customer id
	 * @return true, if successful
	 */
	@Override
	public boolean docDelete(Map<String, Object> payloadJson, int customerId) {
		return false;
	}

	/**
	 * Gets the doc owners.
	 *
	 * @param customerId the customer id
	 * @return the doc owners
	 */
	@Override
	public List<String> getDocOwners(int customerId) {
		return healthDocDao.getDocOwners(customerId);
	}
	
	

}
