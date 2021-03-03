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
import org.springframework.data.relational.core.sql.In;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pb.dp.dao.HealthDocDao;
import com.pb.dp.model.SearchDocFilter;
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
	 * @param customerId the customer id
	 * @return the document list
	 */
	@Override
	public List<Map<String, Object>> getDocumentList(int customerId) {
		return healthDocDao.getDocumentList(customerId);
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
		String url = S3Util.localFileUpload(file);
		healthDoc.setDocS3Url(url);
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
	public boolean docUpdate(MultipartFile file, String payloadJSON, int customerId) throws Exception{

		HealthDoc healthDoc = new HealthDoc();
		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
		healthDoc = mapper.readValue(payloadJSON, HealthDoc.class);
		healthDoc.setCustomerId(customerId);

		boolean isValid = this.healthDocDao.validateDocs(healthDoc.getHealthId(), customerId);
		if(isValid){
			boolean updated = this.healthDocDao.updateDocs(healthDoc, customerId);
			return updated;
		}
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
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
		SearchDocFilter searchDocFilter = mapper.convertValue(payloadJson, SearchDocFilter.class);
		return healthDocDao.getDocs(searchDocFilter, customerId);
	}

	/**
	 * Doc delete.
	 *
	 * @param payloadJson the payload json
	 * @param customerId the customer id
	 * @return true, if successful
	 */
	@Override
	public boolean docDelete(Map<String, Object> payloadJson, int customerId) throws Exception{
		Boolean result = false;
		// hard delete document in db
		result = this.healthDocDao.deleteDocument((Integer) payloadJson.get("id"),customerId);
		// soft delete document in db
		result = this.healthDocDao.softDeleteDocument((Integer) payloadJson.get("id"),customerId);
		return result;
	}

	/**
	 * Gets the doc owners.
	 *
	 * @param customerId the customer id
	 * @return the doc owners
	 */
	@Override
	public List<Map<String, Object>> getDocOwners(int customerId) {
		return healthDocDao.getDocOwners(customerId);
	}
	
	

}
