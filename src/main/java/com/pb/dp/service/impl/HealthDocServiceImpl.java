package com.pb.dp.service.impl;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pb.dp.dao.HealthDocDao;
import com.pb.dp.model.AuthDetail;
import com.pb.dp.model.HealthDoc;
import com.pb.dp.model.SearchDocFilter;
import com.pb.dp.service.ConfigService;
import com.pb.dp.service.HealthDocService;
import com.pb.dp.util.CheckSumUtil;
import com.pb.dp.util.HttpUtil;
import com.pb.dp.util.JsonUtil;
import com.pb.dp.util.S3Util;


/**
 * The Class HealthDocServiceImpl.
 */
@Service
public class HealthDocServiceImpl implements HealthDocService {

	
	private static final Logger logger = LoggerFactory.getLogger(HealthDocServiceImpl.class);
	@Autowired
	private ConfigService configService;
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

		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
		HealthDoc healthDoc = mapper.readValue(payloadJSON, HealthDoc.class);
		healthDoc.setCustomerId(customerId);
		/**consume core api for S3 upload*/
		String url = uploadMiscDoc(file,customerId);
		healthDoc.setDocS3Url(url);
		boolean uploaded = this.healthDocDao.uploadDocs(healthDoc, customerId);
		return uploaded;
	}

	private String uploadMiscDoc(MultipartFile file,int customerId) throws Exception {
		Map<String, Object> request = createCustomerMiscDocRequest(file,customerId);
		String url = configService.getPropertyConfig("UPLOAD_MISC_DOC_URL").getValue();
		Map<String, String> header = new HashMap<>();
		header.put("authKey", configService.getPropertyConfig("CORE_AUTH_KEY").getValue());
		header.put("clientKey", configService.getPropertyConfig("CORE_CLIENT_KEY").getValue());

		Map<String, String> response = HttpUtil.postRequestMultiPartFile(url, request, header);
		String responseAsString = response.get("responseBody");
		Map<String, Object> responseAsMap = JsonUtil.getMapFromJsonString(responseAsString);
		logger.debug("file conversion ", responseAsMap);
		return (String) responseAsMap.get("docUrl");
	}
	
	private Map<String, Object> createCustomerMiscDocRequest(MultipartFile file,int customerId)
			throws NoSuchAlgorithmException {
		Map<String, Object> payloadJson = new HashMap<String, Object>();
		payloadJson.put("type", configService.getPropertyConfig("DOCUMENT_TYPE").getValue());
		payloadJson.put("customerId", customerId);
		payloadJson.put("clientKey", configService.getPropertyConfig("CORE_CLIENT_KEY").getValue());
		payloadJson.put("enquiryId", customerId);
		AuthDetail authDetail = configService.getAuthDetail(configService.getPropertyConfig("CORE_CLIENT_KEY").getValue());
		TreeMap<String, Object> tree = new TreeMap<String, Object>(payloadJson);
		String sign = CheckSumUtil.generateCheckSum(authDetail.getHash_access_key(), authDetail.getHash_secret_key(),
				tree);
		payloadJson.put("signature", sign);
		Map<String, Object> requestMap = new HashMap<String, Object>();
		requestMap.put("payloadJSON", payloadJson);
		requestMap.put("file", file);
		return requestMap;
	}
	/**
	 * Doc update.
	 *
	 * @param id the Database Id
	 * @param healthDoc the object
	 * @return true, if successful
	 */
	@Override
	public boolean docUpdate(Integer id, HealthDoc healthDoc) throws Exception{
		return  healthDocDao.updateDocs(healthDoc, id);
	}

	@Override
	public boolean docUpdateValidate(Integer id, HealthDoc healthDoc) throws Exception{
		boolean isValid = healthDocDao.validateDocs(id, healthDoc.getHealthId(), healthDoc.getCustomerId());
		return isValid ? true : false;
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
