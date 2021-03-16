package com.pb.dp.dao.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import com.pb.dp.dao.HealthLockerDao;
import com.pb.dp.model.Authorise;
import com.pb.dp.model.Subscribe;

@Repository
public class HealthLockerDaoImpl implements HealthLockerDao {

	private static final Logger logger = LoggerFactory.getLogger(HealthLockerDaoImpl.class);

	@Autowired
	private DataSource dataSource;

	//private JdbcTemplate jdbcTemplate;

	private SimpleJdbcInsert insertIntoSubscription;

	private SimpleJdbcInsert insertIntoAuth;
	
	@PostConstruct
	public void init() {
		//jdbcTemplate = new JdbcTemplate(dataSource);
		insertIntoSubscription = new SimpleJdbcInsert(dataSource).withTableName("hl_subscription")
				.usingColumns("healthId","hrpId","reqIdSent","createdAt");
		
		insertIntoAuth = new SimpleJdbcInsert(dataSource).withTableName("hl_auth")
				.usingColumns("healthId","hrpId","reqIdSent","createdAt");
	}

	@Override
	public void insertIntoSubscribe(Subscribe subscribe) throws Exception {
		logger.info("Inserting into subscribe");
		Map<String, Object> params = new HashMap<>();
		params.put("healthId", subscribe.getSubscription().getPatient().getId());
		params.put("hrpId", subscribe.getSubscription().getHiu().getId());
		params.put("reqIdSent", subscribe.getRequestId());
		params.put("createdAt", new Date());
		insertIntoSubscription.execute(params);
	}

	@Override
	public void insertIntoAuth(Authorise authorise) throws Exception {
		logger.info("Inserting into Auth");
		Map<String, Object> params = new HashMap<>();
		params.put("healthId", authorise.getQuery().getId());
		params.put("hrpId", authorise.getQuery().getRequester().getId());
		params.put("reqIdSent", authorise.getRequestId());
		params.put("createdAt", new Date());
		insertIntoAuth.execute(params);
	}

}
