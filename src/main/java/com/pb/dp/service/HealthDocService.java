package com.pb.dp.service;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

public interface HealthDocService {

	List<Map<String, Object>> getDocumentList(int customerId);

	boolean docUpload(MultipartFile file, String payloadJSON, int customerId);

	boolean docUpdate(MultipartFile file, String payloadJSON, int customerId);

	List<Map<String, Object>> docSearch(Map<String, Object> payloadJson, int customerId);

	boolean docDelete(Map<String, Object> payloadJson, int customerId) throws Exception;

	List<String> getDocOwners(int customerId);

}
