package com.pb.dp.dao;

import java.util.Map;

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
	public int isVerified(int otp, Long mobileNo) {
		int cid = 0;
		try {
			Map<String,Object> response = jdbcTemplate.queryForMap(HealthQuery.GET_OTP,mobileNo); 
			int otpFromDb = (int) response.get("otp");
			if(otpFromDb == otp) {
				cid = (int) response.get("id");
			}
		} catch (EmptyResultDataAccessException e) {

		}
		return cid;
	}

	@Override
	public void insertTxnId(int customerId, String txnId) {
		jdbcTemplate.update(HealthQuery.UPDATE_TXN_ID,txnId,customerId);
		
	}

}
