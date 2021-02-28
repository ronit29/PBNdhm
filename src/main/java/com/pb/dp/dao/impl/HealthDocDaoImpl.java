package com.pb.dp.dao.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
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

	@PostConstruct
	public void setJdbcTemplate() {
		this.jdbcTemplate = new JdbcTemplate(this.dataSource);
	}
	
	@Override
	public List<String> getDocOwners(int customerId) {
		return jdbcTemplate.queryForList(HealthQuery.GET_DOC_OWNERS, String.class,customerId);
	}

	@Override
	public boolean uploadDocs(String payloadJson, int customerId) {
//		Map<String, Object> callParams = new HashMap<String, Object>();
		jdbcTemplate.queryForList(HealthQuery.CREATE_DOC, String.class,customerId);

//		SqlParameterSource inParams = new MapSqlParameterSource(callParams);

		return false;
	}
}
