package com.pb.dp.healthIdCreation.dao.impl;

import com.pb.dp.healthIdCreation.dao.HealthIdDao;

import com.pb.dp.healthIdCreation.dao.HealthIdQuery;
import com.pb.dp.healthIdCreation.model.Customer;
import com.pb.dp.healthIdCreation.model.CustomerDetails;
import com.pb.dp.healthIdCreation.model.MobileOtpPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

@Repository
public class HealthIdDaoImpl implements HealthIdDao {

    @Autowired
    private DataSource dataSource;

    private JdbcTemplate jdbcTemplate;

    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @PostConstruct
    public void init() {
        namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public void addCustomer(CustomerDetails customerDetail) throws Exception {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        //add customer address details
        Map<String,Object> addressParams = new HashMap<String,Object>();
        addressParams.put("line1",customerDetail.getAddress());
        addressParams.put("districtId", customerDetail.getDistrict());
        addressParams.put("stateId",customerDetail.getState());
        Integer addressCount = this.namedParameterJdbcTemplate.update(HealthIdQuery.ADD_CUSTOMER_ADDRESS,addressParams);

        //add customer details
        Map<String,Object> custParams = new HashMap<String,Object>();
        custParams.put("firstName",customerDetail.getFirstName());
        custParams.put("lastName",customerDetail.getLastName());
        custParams.put("dob",formatter.parse(customerDetail.getDob()));
        custParams.put("relation",customerDetail.getRelationship());
        custParams.put("email",customerDetail.getEmailId());
        custParams.put("gender",customerDetail.getGender());
        custParams.put("mobile", customerDetail.getMobileNo());
        Integer custCount  =  this.namedParameterJdbcTemplate.update(HealthIdQuery.ADD_CUSTOMER,custParams);

    }

    @Override
    public void addNdhmOtpTxnId(long mobileNo, String txnId) {
        //add txnId in ndhm_otp
        Map<String,Object> params = new HashMap<String,Object>();
        params.put("mobile",mobileNo);
        params.put("txnId", txnId);
        Integer addressCount = this.namedParameterJdbcTemplate.update(HealthIdQuery.ADD_NDHM_OTP_TXNID,params);

    }

    @Override
    public void updateNdhmOTP(MobileOtpPojo mobileOtpPojo) {
        //update otp in ndhm_otp
        Map<String,Object> params = new HashMap<String,Object>();
        params.put("mobile",mobileOtpPojo.getMobile());
        params.put("txnId", mobileOtpPojo.getTxnId());
        params.put("otp", mobileOtpPojo.getOtp());
        Integer updateCount = this.namedParameterJdbcTemplate.update(HealthIdQuery.UPDATE_NDHM_OTP,params);

    }

    @Override
    public void updateNdhmOtpToken(MobileOtpPojo mobileOtpPojo) {
        //update token in ndhm_otp
        Map<String,Object> params = new HashMap<String,Object>();
        params.put("mobile",mobileOtpPojo.getMobile());
        params.put("txnId", mobileOtpPojo.getTxnId());
        params.put("token", mobileOtpPojo.getToken());
        Integer updateCount = this.namedParameterJdbcTemplate.update(HealthIdQuery.UPDATE_NDHM_OTP_TOKEN,params);

    }

    @Override
    public Customer getCustomer(Integer custId, Long mobile) {
        Map<String,Object> params = new HashMap<String,Object>();
        params.put("id",custId);
        params.put("mobile", mobile);
        Customer customer = this.namedParameterJdbcTemplate.queryForObject(HealthIdQuery.GET_CUSTOMER,params,new Customer.CustomerRowMapper());
        return customer;
    }
}
