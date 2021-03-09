package com.pb.dp.dao;

import java.util.List;
import java.util.Map;

import com.pb.dp.model.HealthDoc;
import com.pb.dp.model.SearchDocFilter;
import org.springframework.dao.DataAccessException;


public interface HealthDocDao {

	boolean uploadDocs(HealthDoc healthDoc, int customerId);

	boolean validateDocs(Integer id, String healthId, long customerId);

	boolean updateDocs(HealthDoc healthDoc, int id);

	List<Map<String, Object>> getDocOwners(int customerId);

	List<Map<String, Object>> getDocs(SearchDocFilter searchDocFilter, int customerId);

	List<Map<String, Object>> getDocumentList(Integer customerId);

	Boolean deleteDocument(Integer id,Integer customerId) throws Exception;

	Boolean softDeleteDocument(Integer id,Integer customerId) throws Exception, DataAccessException;

}
