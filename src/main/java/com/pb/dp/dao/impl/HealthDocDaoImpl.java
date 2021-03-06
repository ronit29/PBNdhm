package com.pb.dp.dao.impl;

import java.util.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import com.pb.dp.dao.HealthIdQuery;
import com.pb.dp.dao.HealthDocQuery;
import com.pb.dp.model.HealthDoc;
import com.pb.dp.model.HealthId;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import org.springframework.stereotype.Repository;

import com.pb.dp.dao.HealthDocDao;
import com.pb.dp.dao.HealthQuery;
import com.pb.dp.model.SearchDocFilter;

/**
 * The Class HealthDocDaoImpl.
 */
@Repository
public class HealthDocDaoImpl implements HealthDocDao {

	@Autowired
	private DataSource dataSource;

	private JdbcTemplate jdbcTemplate;
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	@PostConstruct
	public void setJdbcTemplate() {
		this.jdbcTemplate = new JdbcTemplate(this.dataSource);
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	@Override
	public List<Map<String,Object>> getDocOwners(int customerId) {
		return jdbcTemplate.queryForList(HealthQuery.GET_DOC_OWNERS,customerId);
	}

	@Override
	public List<Map<String, Object>> getDocs(SearchDocFilter searchDocFilter, int customerId) {
		StringBuilder queryBuilder = new StringBuilder(HealthQuery.GET_DOCS);
		MapSqlParameterSource searchParams = new MapSqlParameterSource();
		searchParams.addValue("customerId", customerId);
		if(null != searchDocFilter.getDocId()) {
			queryBuilder.append(" and healthId = :docId");
			searchParams.addValue("docId", searchDocFilter.getDocId());
		}
		if(null != searchDocFilter.getDocName() && !searchDocFilter.getDocName().isEmpty()) {
			queryBuilder.append(" and docName = :docName");
			searchParams.addValue("docName", searchDocFilter.getDocName());
		}
		if(null != searchDocFilter.getMedicEntityName() && !searchDocFilter.getMedicEntityName().isEmpty()) {
			queryBuilder.append(" and medicEntityName = :medicEntityName");
			searchParams.addValue("medicEntityName", searchDocFilter.getMedicEntityName());
		}
		if(null != searchDocFilter.getDocType()) {
			queryBuilder.append(" and docTypeId = :docTypeId");
			searchParams.addValue("docTypeId", searchDocFilter.getDocType());
		}
		if(null != searchDocFilter.getUpdatedFrom()) {
			queryBuilder.append(" and hd.updatedAt >= :updatedFrom");
			searchParams.addValue("updatedFrom", searchDocFilter.getUpdatedFrom());
		}
		if(null != searchDocFilter.getUpdatedTo()) {
			queryBuilder.append(" and hd.updatedAt <= :updatedTo");
			searchParams.addValue("updatedTo", searchDocFilter.getUpdatedTo());
		}
		if(null != searchDocFilter.getTags() && !searchDocFilter.getTags().isEmpty()) {
			queryBuilder.append(" and CONCAT(',',docTags,',') LIKE CONCAT('%,',:docTags,',%')");
			searchParams.addValue("docTags", searchDocFilter.getTags());
		}
		
		List<Map<String, Object>> responseFromDb = namedParameterJdbcTemplate.queryForList(queryBuilder.toString(), searchParams); 
		SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy hh:mm:ss");
		responseFromDb.stream().forEach(e->{
			if(ObjectUtils.isNotEmpty(e.get("createdAt")))
				e.put("createdAtStr",sdf.format((Timestamp)e.get("createdAt")));
			if(ObjectUtils.isNotEmpty(e.get("updatedAt")))
				e.put("updatedAtStr",sdf.format((Timestamp)e.get("updatedAt")));
		});
		return responseFromDb;
		
	}

	@Override
	public List<Map<String, Object>> getDocumentList(Integer customerId) {
		Map<String,Object> params = new HashMap<>();
		params.put("customerId",customerId);
		 List<Map<String,Object>> resultMap = namedParameterJdbcTemplate.queryForList(HealthDocQuery.GET_HEALTH_DOC_LIST,params);
		SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy hh:mm:ss");
		resultMap.stream().forEach(e->{
			if(ObjectUtils.isNotEmpty(e.get("createdAt")))
				e.put("createdAtStr",sdf.format((Timestamp)e.get("createdAt")));
			if(ObjectUtils.isNotEmpty(e.get("updatedAt")))
				e.put("updatedAtStr",sdf.format((Timestamp)e.get("updatedAt")));
		});
		 return resultMap;
	}

	@Override
	public Boolean deleteDocument(Integer id, Integer customerId) throws Exception	{
		Map<String,Object> params = new HashMap<>();
		params.put("id",id);
		Integer updateCount = namedParameterJdbcTemplate.update(HealthDocQuery.DELETE_HEALTH_DOC,params);
		if(updateCount.equals(1))
			return true;
		return false;
	}

	@Override
	public Boolean softDeleteDocument(Integer id, Integer customerId) throws Exception, DataAccessException {
		Map<String,Object> params = new HashMap<>();
		params.put("id",id);
		params.put("customerId",customerId);
		Integer updateCount = (Integer)this.namedParameterJdbcTemplate.update(HealthDocQuery.SOFT_DELETE_HEALTH_DOC,params);
		if(updateCount.equals(1))
			return true;
		return false;
	}

	@Override
	public boolean uploadDocs(HealthDoc healthDoc, int customerId) {
		MapSqlParameterSource docParams = new MapSqlParameterSource();
		docParams.addValue("docName", healthDoc.getDocName());
		docParams.addValue("docOwner",healthDoc.getDocOwner());
		docParams.addValue("docTypeId",healthDoc.getDocTypeId());
		docParams.addValue("docS3Url",healthDoc.getDocS3Url());
		docParams.addValue("docTags",healthDoc.getDocTags());
		docParams.addValue("medicEntityName",healthDoc.getMedicEntityName());
		docParams.addValue("doctorName",healthDoc.getDoctorName());
		docParams.addValue("customerId",customerId);
		docParams.addValue("healthId",healthDoc.getHealthId());
		int rowCount = namedParameterJdbcTemplate.update(HealthQuery.CREATE_DOCUMENT, docParams);
		return rowCount>0;
	}

	@Override
	public boolean validateDocs(Integer id, String healthId, long customerId) {
		MapSqlParameterSource docParams = new MapSqlParameterSource();
		docParams.addValue("customerId",customerId);
		docParams.addValue("healthId",healthId);
		docParams.addValue("id",id);
		int count  = namedParameterJdbcTemplate.queryForObject(HealthQuery.VALIDATE_DOCUMENT, docParams, Integer.class);
		return count>0;
	}

	@Override
	public boolean updateDocs(HealthDoc healthDoc, int id) {
		MapSqlParameterSource docParams = new MapSqlParameterSource();
		docParams.addValue("id",id);
		docParams.addValue("docName", healthDoc.getDocName());
		docParams.addValue("docOwner",healthDoc.getDocOwner());
		docParams.addValue("docTypeId",healthDoc.getDocTypeId());
		docParams.addValue("docS3Url",healthDoc.getDocS3Url());
		docParams.addValue("docTags",healthDoc.getDocTags());
		docParams.addValue("medicEntityName",healthDoc.getMedicEntityName());
		docParams.addValue("doctorName",healthDoc.getDoctorName());
		docParams.addValue("customerId",healthDoc.getCustomerId());
		docParams.addValue("healthId",healthDoc.getHealthId());
		int count = this.namedParameterJdbcTemplate.update(HealthQuery.UPDATE_DOCUMENT, docParams);
		return count>0;
	}

	@Override
	public Map<String,Object> getSubscriptionData(String healthId) {
		MapSqlParameterSource subParams = new MapSqlParameterSource();
		subParams.addValue("healthId",healthId);

		Map<String,Object> resp = this.namedParameterJdbcTemplate.queryForObject(HealthDocQuery.GET_SUBSCRIPTION, subParams, Map.class);
		return resp;
	}
}
