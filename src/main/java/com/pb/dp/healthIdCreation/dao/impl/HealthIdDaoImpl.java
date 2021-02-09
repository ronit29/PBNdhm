package com.pb.dp.healthIdCreation.dao.impl;

import com.pb.dp.healthIdCreation.dao.HealthIdDao;

import com.pb.dp.healthIdCreation.dao.HealthIdQuery;
import com.pb.dp.healthIdCreation.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
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
    public Integer addHealthIdDemographics(CustomerDetails customerDetail, int customerId) throws Exception {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        KeyHolder addKeyHolder = new GeneratedKeyHolder();
        //add customer address details
        MapSqlParameterSource addressParams = new MapSqlParameterSource();
        addressParams.addValue("line1",customerDetail.getAddress());
        addressParams.addValue("districtId", customerDetail.getDistrict());
        addressParams.addValue("stateId",customerDetail.getState());
        Integer addressCount = this.namedParameterJdbcTemplate.update(HealthIdQuery.ADD_CUSTOMER_ADDRESS,addressParams,addKeyHolder);
        Integer addressId = addKeyHolder.getKey().intValue();
        //add healthId demographic details
        KeyHolder custKeyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource custParams = new MapSqlParameterSource();
        custParams.addValue("custId",customerId);
        custParams.addValue("firstName",customerDetail.getFirstName());
        custParams.addValue("lastName",customerDetail.getLastName());
        custParams.addValue("dob",formatter.parse(customerDetail.getDob()));
        custParams.addValue("relation",customerDetail.getRelationId());
        custParams.addValue("address",addressId);
        custParams.addValue("email",customerDetail.getEmailId());
        custParams.addValue("gender",customerDetail.getGender());
//        custParams.addValue("mobile", customerDetail.getMobileNo());
        custParams.addValue("healthId",customerDetail.getHealthId());
//        Integer custCount  =  this.namedParameterJdbcTemplate.update(HealthIdQuery.UPDATE_CUSTOMER_DETAILS,custParams);
        Integer custCount  =  this.namedParameterJdbcTemplate.update(HealthIdQuery.ADD_HEALTH_ID_DEMOGRAPHIC,custParams,custKeyHolder);
        Integer healthIdPk = custKeyHolder.getKey().intValue();
        return healthIdPk;
    }

    @Override
    public void addNdhmOtpTxnId(long mobileNo, String txnId) throws Exception {
        //add txnId in ndhm_otp
        Map<String,Object> params = new HashMap<String,Object>();
        params.put("mobile",mobileNo);
        params.put("txnId", txnId);
        Integer addressCount = this.namedParameterJdbcTemplate.update(HealthIdQuery.ADD_NDHM_OTP_TXNID,params);

    }

    @Override
    public void updateNdhmOTP(NdhmMobOtpRequest ndhmMobOtpRequest) throws Exception{
        //update otp in ndhm_otp
        Map<String,Object> params = new HashMap<String,Object>();
        params.put("mobile", ndhmMobOtpRequest.getMobile());
        params.put("txnId", ndhmMobOtpRequest.getTxnId());
        params.put("otp", ndhmMobOtpRequest.getOtp());
        Integer updateCount = this.namedParameterJdbcTemplate.update(HealthIdQuery.UPDATE_NDHM_OTP,params);

    }

    @Override
    public void updateNdhmOtpToken(NdhmMobOtpRequest ndhmMobOtpRequest, Integer custId) throws Exception{
        //update ndhm mobile token in healthId
        Map<String,Object> params = new HashMap<String,Object>();
        params.put("custId",custId);
//        params.put("mobile", ndhmMobOtpRequest.getMobile());
        params.put("txnId", ndhmMobOtpRequest.getTxnId());
        params.put("token", ndhmMobOtpRequest.getToken());
 //       Integer updateCount = this.namedParameterJdbcTemplate.update(HealthIdQuery.UPDATE_NDHM_MOBILE_TOKEN,params);
        Integer updateCount = this.namedParameterJdbcTemplate.update(HealthIdQuery.UPDATE_HEALTH_ID_MOB_TOKEN,params);

    }

    @Override
    public CustomerDetails getCustomerDetails(Integer custId, Long mobile, String txnId) throws Exception{
        Map<String,Object> params = new HashMap<String,Object>();
        params.put("custId",custId);
        params.put("txnId",txnId);
        HealthId healthId = this.namedParameterJdbcTemplate.queryForObject(HealthIdQuery.GET_HEALTH_ID_By_TXN_AND_CUST_ID,params,new HealthId.HealthIdRowMapper());
        params.clear();
        params.put("id",healthId.getAddressId());
        Address address = this.namedParameterJdbcTemplate.queryForObject(HealthIdQuery.GET_ADDRESS,params,new Address.AddressRowMapper());
        CustomerDetails customerDetails = new CustomerDetails();
        customerDetails.setFirstName(healthId.getFirstName());
        customerDetails.setLastName(healthId.getLastName());
        customerDetails.setEmailId(healthId.getEmail());
        customerDetails.setHealthId(healthId.getHealthId());
        customerDetails.setMobileNo(mobile);
        customerDetails.setGender(healthId.getGender());
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        customerDetails.setDob(formatter.format(healthId.getDob()));
        customerDetails.setAddress(address.getLine1());
        customerDetails.setState(address.getStateId());
        customerDetails.setDistrict(address.getDistrictId());
        return customerDetails;
    }

    @Override
    public void updateNdhmTxnId(Integer id, String txnId) throws Exception{
        //update txnId in customer
        Map<String,Object> params = new HashMap<String,Object>();
        params.put("id",id);
        params.put("txnId", txnId);
//        Integer updateCount = this.namedParameterJdbcTemplate.update(HealthIdQuery.UPDATE_NDHM_TXN_ID,params);
        Integer updateCount = this.namedParameterJdbcTemplate.update(HealthIdQuery.UPDATE_HEALTH_ID_TXN_ID,params);
    }

    @Override
    public Long addNewCustomer(Long mobile, int otp) throws Exception{
        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource custParams = new MapSqlParameterSource();
        //custParams.addValue("firstName","New");
        custParams.addValue("otp",otp);
        custParams.addValue("mobile", mobile);
        Integer custCount  =  this.namedParameterJdbcTemplate.update(HealthIdQuery.ADD_CUSTOMER,custParams,keyHolder);
        Long custId = keyHolder.getKey().longValue() ;
        return custId;
    }

    @Override
    public void updateProfileData(CustomerDetails customerDetail, int customerId) throws ParseException {
        //update address details
        MapSqlParameterSource addressParams = new MapSqlParameterSource();
        addressParams.addValue("address",customerDetail.getAddress());
        addressParams.addValue("districtId", customerDetail.getDistrict());
        addressParams.addValue("stateId",customerDetail.getState());
        addressParams.addValue("pincode",customerDetail.getPincode());
        addressParams.addValue("custId",customerId);
        addressParams.addValue("healthId",customerDetail.getHealthId());
        Integer addressCount = this.namedParameterJdbcTemplate.update(HealthIdQuery.UPDATE_PROFILE_ADDRESS,addressParams);
        //update customer details
        MapSqlParameterSource custParams = new MapSqlParameterSource();
        custParams.addValue("custId",customerId);
        custParams.addValue("healthId",customerDetail.getHealthId());
        custParams.addValue("firstName",customerDetail.getFirstName());
        custParams.addValue("lastName",customerDetail.getLastName());
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        custParams.addValue("dob",formatter.parse(customerDetail.getDob()));
        custParams.addValue("relation",customerDetail.getRelationId());
        custParams.addValue("email",customerDetail.getEmailId());
        custParams.addValue("gender",customerDetail.getGender());
        Integer custCount  =  this.namedParameterJdbcTemplate.update(HealthIdQuery.UPDATE_PROFILE_CUSTOMER,custParams);
    }

    @Override
    public void addHealthIdData(CustomerDetails customerDetail, int customerId, String txnId) throws Exception {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("healthId",customerDetail.getHealthId());
        params.addValue("healtIdNo", customerDetail.getHealthIdNo());
        params.addValue("custId",customerId);
        params.addValue("token",customerDetail.getToken());
        params.addValue("txnId",txnId);
        //nteger recordCount = this.namedParameterJdbcTemplate.update(HealthIdQuery.ADD_HEALTH_ID,params);
        Integer recordCount = this.namedParameterJdbcTemplate.update(HealthIdQuery.UPDATE_HEALTHID_DATA,params);

    }

    @Override
    public Customer getCustomerByMobile(Long mobile) throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("mobile", mobile);
        try {
            Customer customer = this.namedParameterJdbcTemplate.queryForObject(HealthIdQuery.GET_CUSTOMER_BY_MOBILE, params, new Customer.CustomerRowMapper());
            return customer;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public void updateCustomer(Long mobile, int otp, long id)  throws Exception{
        MapSqlParameterSource custParams = new MapSqlParameterSource();
        custParams.addValue("id",id);
        custParams.addValue("otp",otp);
        custParams.addValue("mobile", mobile);
        Integer custCount  =  this.namedParameterJdbcTemplate.update(HealthIdQuery.UPDATE_CUSTOMER,custParams);
    }

    @Override
    public HealthId getHealthIdDetails(int customerId, Integer relation) throws Exception{
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("custId", customerId);
        params.put("relation", relation);
        try {
            HealthId healthId = this.namedParameterJdbcTemplate.queryForObject(HealthIdQuery.GET_HEALTH_ID_BY_RELATION_AND_CUST_ID, params, new HealthId.HealthIdRowMapper());
            return healthId;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public void updateProfileTxnId(int customerId, String healthId, String txnId) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("custId", customerId);
        params.put("healthId", healthId);
        params.put("txnId", txnId);
        Integer count  =  this.namedParameterJdbcTemplate.update(HealthIdQuery.UPDATE_PROFILE_TXN_ID,params);
    }
}
