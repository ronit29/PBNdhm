package com.pb.dp.dao.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import com.pb.dp.dao.HealthIdQuery;
import com.pb.dp.model.HealthDoc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import com.pb.dp.dao.HealthDocDao;
import com.pb.dp.dao.HealthQuery;

/**
 * The Class HealthDocDaoImpl.
 */
@Repository
public class HealthDocDaoImpl implements HealthDocDao{

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
	public List<String> getDocOwners(int customerId) {
		return jdbcTemplate.queryForList(HealthQuery.GET_DOC_OWNERS, String.class,customerId);
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
		this.namedParameterJdbcTemplate.update(HealthQuery.CREATE_DOCUMENT, docParams);
		return true;
	}
}
