package com.pb.dp.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
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
	public boolean docUpload(MultipartFile file, String payloadJSON, int customerId) {
		return false;
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
