package com.pb.dp.healthIdCreation.dao.impl;

import com.pb.dp.healthIdCreation.dao.HealthIdDao;

import com.pb.dp.healthIdCreation.dao.HealthIdQuery;
import com.pb.dp.healthIdCreation.model.Address;
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
        custParams.addValue("healthId",customerDetail.getHealthId());
        Integer custCount  =  this.namedParameterJdbcTemplate.update(HealthIdQuery.UPDATE_CUSTOMER_DETAILS,custParams);
        return customerId;
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
        //update ndhm mobile token in customer
        Map<String,Object> params = new HashMap<String,Object>();
        params.put("custId",custId);
        params.put("mobile", ndhmMobOtpRequest.getMobile());
//        params.put("txnId", ndhmMobOtpRequest.getTxnId());
        params.put("token", ndhmMobOtpRequest.getToken());
        Integer updateCount = this.namedParameterJdbcTemplate.update(HealthIdQuery.UPDATE_NDHM_MOBILE_TOKEN,params);

    }

    @Override
    public CustomerDetails getCustomerDetails(Integer custId) throws Exception{
        Map<String,Object> params = new HashMap<String,Object>();
        params.put("id",custId);
        Customer customer = this.namedParameterJdbcTemplate.queryForObject(HealthIdQuery.GET_CUSTOMER,params,new Customer.CustomerRowMapper());
        params.clear();
        params.put("id",customer.getAddressId());
        Address address = this.namedParameterJdbcTemplate.queryForObject(HealthIdQuery.GET_ADDRESS,params,new Address.AddressRowMapper());
        CustomerDetails customerDetails = new CustomerDetails();
        customerDetails.setFirstName(customer.getFirstName());
        customerDetails.setLastName(customer.getLastName());
        customerDetails.setEmailId(customer.getEmailId());
        customerDetails.setHealthId(customer.getHealthId());
        customerDetails.setMobileNo(customer.getMobile());
        customerDetails.setGender(customer.getGender());
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        customerDetails.setDob(formatter.format(customer.getDob()));
        customerDetails.setAddress(address.getLine1());
        customerDetails.setState(address.getStateId());
        customerDetails.setDistrict(address.getDistrictId());
        return customerDetails;
    }

    @Override
    public void updateNdhmTxnId(Integer custId, String txnId) throws Exception{
        //update txnId in customer
        Map<String,Object> params = new HashMap<String,Object>();
        params.put("custId",custId);
        params.put("txnId", txnId);
        Integer updateCount = this.namedParameterJdbcTemplate.update(HealthIdQuery.UPDATE_NDHM_TXN_ID,params);
    }

    @Override
    public Integer addNewCustomer(Long mobile, int otp) throws Exception{
        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource custParams = new MapSqlParameterSource();
        custParams.addValue("firstName","New");
        custParams.addValue("otp",otp);
        custParams.addValue("mobile", mobile);
        Integer custCount  =  this.namedParameterJdbcTemplate.update(HealthIdQuery.ADD_CUSTOMER,custParams,keyHolder);
        Integer custId = keyHolder.getKey().intValue();
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
        Integer addressCount = this.namedParameterJdbcTemplate.update(HealthIdQuery.UPDATE_PROFILE_ADDRESS,addressParams);
        //update customer details
        MapSqlParameterSource custParams = new MapSqlParameterSource();
        custParams.addValue("custId",customerId);
        custParams.addValue("firstName",customerDetail.getFirstName());
        custParams.addValue("lastName",customerDetail.getLastName());
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        custParams.addValue("dob",formatter.parse(customerDetail.getDob()));
        custParams.addValue("relation",customerDetail.getRelationship());
        custParams.addValue("email",customerDetail.getEmailId());
        custParams.addValue("gender",customerDetail.getGender());
        Integer custCount  =  this.namedParameterJdbcTemplate.update(HealthIdQuery.UPDATE_PROFILE_CUSTOMER,custParams);
    }

    @Override
    public void addHealthIdData(CustomerDetails customerDetail, int customerId) throws Exception {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("healthId",customerDetail.getAddress());
        params.addValue("healtIdNo", customerDetail.getDistrict());
        params.addValue("custId",customerId);
        params.addValue("token",customerDetail.getToken());
        Integer addressCount = this.namedParameterJdbcTemplate.update(HealthIdQuery.ADD_HEALTH_ID,params);
    }
}
