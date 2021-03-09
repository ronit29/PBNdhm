package com.pb.dp.service;

import java.util.List;
import java.util.Map;

import com.pb.dp.model.HealthDoc;
import org.springframework.web.multipart.MultipartFile;

public interface HealthDocService {

	List<Map<String, Object>> getDocumentList(int customerId);

	boolean docUpload(MultipartFile file, String payloadJSON, int customerId) throws Exception;

	boolean docUpdateValidate(Integer id, HealthDoc healthDoc) throws Exception;

	boolean docUpdate(Integer id, HealthDoc healthDoc) throws Exception;

	List<Map<String, Object>> docSearch(Map<String, Object> payloadJson, int customerId);

	boolean docDelete(Integer id, int customerId) throws Exception;

	List<Map<String, Object>> getDocOwners(int customerId);

}
