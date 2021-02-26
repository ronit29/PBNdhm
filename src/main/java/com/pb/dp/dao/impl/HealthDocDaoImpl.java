package com.pb.dp.dao.impl;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
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

}
