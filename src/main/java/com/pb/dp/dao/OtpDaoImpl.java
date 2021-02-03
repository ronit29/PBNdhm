package com.pb.dp.dao;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class OtpDaoImpl implements OtpDao {

	@Autowired
	private DataSource dataSource;

	private JdbcTemplate jdbcTemplate;

	@PostConstruct
	public void init() {
		jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public boolean isVerified(int otp, Long mobileNo) {
		boolean isVerified = false;
		try {
			int otpFromDb = jdbcTemplate.queryForObject(HealthQuery.GET_OTP, Integer.class,mobileNo);
			if(otpFromDb == otp) {
				isVerified = true;
			}
		} catch (EmptyResultDataAccessException e) {

		}
		return isVerified;
	}

	@Override
	public void insertTxnId(int customerId, String txnId) {
		jdbcTemplate.update(HealthQuery.UPDATE_TXN_ID,txnId,customerId);
		
	}

}
