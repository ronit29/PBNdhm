package com.pb.dp.dao.impl;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.pb.dp.dao.HealthLockerCallBackDao;
import com.pb.dp.dao.HealthQuery;

@Repository
public class HealthLockerCallBackDaoImpl implements HealthLockerCallBackDao {

	@Autowired
	private DataSource dataSource;

	private JdbcTemplate jdbcTemplate;

	@PostConstruct
	public void setJdbcTemplate() {
		this.jdbcTemplate = new JdbcTemplate(this.dataSource);
	}
	@Override
	public boolean updateCallBackSubscribe(String requestId, String subscriptionId,String reqIdSent) throws Exception {
		boolean isUpdated = false;
		if(null!=reqIdSent) {
			int updatedRows = jdbcTemplate.update(HealthQuery.UPDATE_HL_SUBS, requestId,subscriptionId,reqIdSent);
			isUpdated = updatedRows>0;
		}
		return isUpdated;
	}
	@Override
	public boolean updateCallBackAuth(String requestId, String transactionId, String reqIdSent) throws Exception {
		boolean isUpdated = false;
		if(null!=reqIdSent) {
			int updatedRows = jdbcTemplate.update(HealthQuery.UPDATE_HL_AUTH, requestId,transactionId,reqIdSent);
			isUpdated = updatedRows>0;
		}
		return isUpdated;
	}

}
