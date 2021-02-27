package com.pb.dp.dao.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
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
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(this.dataSource);
	}

	@Override
	public List<String> getDocOwners(int customerId) {
		return jdbcTemplate.queryForList(HealthQuery.GET_DOC_OWNERS, String.class, customerId);
	}

	@Override
	public List<Map<String, Object>> getDocs(SearchDocFilter searchDocFilter, int customerId) {
		StringBuilder queryBuilder = new StringBuilder(HealthQuery.GET_DOCS);
		MapSqlParameterSource searchParams = new MapSqlParameterSource();
		searchParams.addValue("customerId", customerId);
		if(null != searchDocFilter.getDocOwner()) {
			queryBuilder.append(" and docOwner = :docOwner");
			searchParams.addValue("docOwner", searchDocFilter.getDocOwner());
		}
		if(null != searchDocFilter.getDocName()) {
			queryBuilder.append(" and docName = :docName");
			searchParams.addValue("docName", searchDocFilter.getDocName());
		}
		if(null != searchDocFilter.getTags()) {
			queryBuilder.append(" and CONCAT(',',docTags,',') LIKE CONCAT('%,',:docTags,',%')");
			searchParams.addValue("docTags", searchDocFilter.getTags());
		}
		return namedParameterJdbcTemplate.queryForList(queryBuilder.toString(), searchParams);
	}

}
