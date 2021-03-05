package com.pb.dp.dao;

import java.util.List;
import java.util.Map;

import com.pb.dp.model.HealthDoc;
import com.pb.dp.model.SearchDocFilter;


public interface HealthDocDao {

	boolean uploadDocs(HealthDoc healthDoc, int customerId);

	boolean validateDocs(String healthId, int customerId);

	boolean updateDocs(HealthDoc healthDoc, int customerId);

	List<Map<String, Object>> getDocOwners(int customerId);

	List<Map<String, Object>> getDocs(SearchDocFilter searchDocFilter, int customerId);

	List<Map<String, Object>> getDocumentList(Integer customerId);

	Boolean deleteDocument(Integer id,Integer customerId) throws Exception;

	Boolean softDeleteDocument(Integer id,Integer customerId) throws Exception;

}
