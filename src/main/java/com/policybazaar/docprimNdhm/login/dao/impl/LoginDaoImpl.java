package com.policybazaar.docprimNdhm.login.dao.impl;

import com.policybazaar.docprimNdhm.login.dao.LoginDao;
import com.policybazaar.docprimNdhm.login.service.LoginQuery;
import org.springframework.beans.factory.annotation.Autowired;
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
        return jdbcTemplate.update(LoginQuery.INSERT_OTP_DETAILS, new Object[]{countryCode, mobile, message, smsResponse, smsType, uuid});
    }
}
