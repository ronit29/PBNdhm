package com.pb.dp.healthIdCreation.dao.impl;

import com.pb.dp.healthIdCreation.dao.HealthIdDao;

import com.pb.dp.healthIdCreation.dao.HealthIdQuery;
import com.pb.dp.healthIdCreation.model.Customer;
import com.pb.dp.healthIdCreation.model.CustomerDetails;
import com.pb.dp.healthIdCreation.model.NdhmMobOtpRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
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
    public Integer addCustomer(CustomerDetails customerDetail, int customerId) throws Exception {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        KeyHolder addKeyHolder = new GeneratedKeyHolder();
        //add customer address details
        MapSqlParameterSource addressParams = new MapSqlParameterSource();
        addressParams.addValue("line1",customerDetail.getAddress());
        addressParams.addValue("districtId", customerDetail.getDistrict());
        addressParams.addValue("stateId",customerDetail.getState());
        Integer addressCount = this.namedParameterJdbcTemplate.update(HealthIdQuery.ADD_CUSTOMER_ADDRESS,addressParams,addKeyHolder);
        Integer addressId = addKeyHolder.getKey().intValue();
        //add customer details
        MapSqlParameterSource custParams = new MapSqlParameterSource();
        custParams.addValue("custId",customerId);
        custParams.addValue("firstName",customerDetail.getFirstName());
        custParams.addValue("lastName",customerDetail.getLastName());
        custParams.addValue("dob",formatter.parse(customerDetail.getDob()));
        custParams.addValue("relation",customerDetail.getRelationship());
        custParams.addValue("address",addressId);
        custParams.addValue("email",customerDetail.getEmailId());
        custParams.addValue("gender",customerDetail.getGender());
        custParams.addValue("mobile", customerDetail.getMobileNo());
        Integer custCount  =  this.namedParameterJdbcTemplate.update(HealthIdQuery.UPDATE_CUSTOMER_DETAILS,custParams);
        return customerId;
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
    public void updateNdhmOTP(NdhmMobOtpRequest ndhmMobOtpRequest) {
        //update otp in ndhm_otp
        Map<String,Object> params = new HashMap<String,Object>();
        params.put("mobile", ndhmMobOtpRequest.getMobile());
        params.put("txnId", ndhmMobOtpRequest.getTxnId());
        params.put("otp", ndhmMobOtpRequest.getOtp());
        Integer updateCount = this.namedParameterJdbcTemplate.update(HealthIdQuery.UPDATE_NDHM_OTP,params);

    }

    @Override
    public void updateNdhmOtpToken(NdhmMobOtpRequest ndhmMobOtpRequest, Integer custId) throws Exception{
        //update ndhm mobile token in customer
        Map<String,Object> params = new HashMap<String,Object>();
        params.put("custId",custId);
        params.put("mobile", ndhmMobOtpRequest.getMobile());
//        params.put("txnId", ndhmMobOtpRequest.getTxnId());
        params.put("token", ndhmMobOtpRequest.getToken());
//        Integer updateCount = this.namedParameterJdbcTemplate.update(HealthIdQuery.UPDATE_NDHM_OTP_TOKEN,params);
        Integer updateCount = this.namedParameterJdbcTemplate.update(HealthIdQuery.UPDATE_NDHM_MOBILE_TOKEN,params);

    }

    @Override
    public Customer getCustomer(Integer custId, Long mobile) {
        Map<String,Object> params = new HashMap<String,Object>();
        params.put("id",custId);
        params.put("mobile", mobile);
        Customer customer = this.namedParameterJdbcTemplate.queryForObject(HealthIdQuery.GET_CUSTOMER,params,new Customer.CustomerRowMapper());
        return customer;
    }

    @Override
    public void updateNdhmTxnId(Integer custId, String txnId) {
        //update txnId in customer
        Map<String,Object> params = new HashMap<String,Object>();
        params.put("custId",custId);
        params.put("txnId", txnId);
        Integer updateCount = this.namedParameterJdbcTemplate.update(HealthIdQuery.UPDATE_NDHM_TXN_ID,params);
    }
}
