package com.pb.dp.dao;

import com.pb.dp.service.LoginQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

@Repository
public class LoginDaoImpl implements LoginDao {

    @Autowired
    private DataSource dataSource;

    private JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void init() {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public Integer inserOtpDetails(int otp, Integer countryCode, Long mobile, String message, String smsResponse, int smsType, String uuid) {
        return jdbcTemplate.update(LoginQuery.INSERT_OTP_DETAILS, new Object[]{countryCode, mobile, otp, message, smsResponse, smsType, uuid});
    }

	@Override
	public String getCustomerName(Long mobile) {
		String name;
		try {
			name = jdbcTemplate.queryForObject(LoginQuery.GET_CUST_NAME, String.class, mobile);
			
		}catch (EmptyResultDataAccessException e) {
			name = "User";
		}
		return name;
	}
}
