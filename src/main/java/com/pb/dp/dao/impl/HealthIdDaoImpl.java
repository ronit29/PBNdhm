package com.pb.dp.dao.impl;

import com.pb.dp.dao.HealthIdDao;

import com.pb.dp.dao.HealthIdQuery;
import com.pb.dp.model.Customer;
import com.pb.dp.model.CustomerDetails;
import com.pb.dp.model.HealthId;
import com.pb.dp.util.HelperUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
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
    HelperUtil helperUtil;

    @Autowired
    private DataSource dataSource;

    private JdbcTemplate jdbcTemplate;

    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private static final Logger logger = LoggerFactory.getLogger(HealthIdDaoImpl.class);

    @PostConstruct
    public void init() {
        namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public Integer addHealthIdDemographics(CustomerDetails customerDetail, int customerId) throws Exception {
        Integer healthIdPk = 0;
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        KeyHolder addKeyHolder = new GeneratedKeyHolder();
        //add customer address details
        MapSqlParameterSource addressParams = new MapSqlParameterSource();
        addressParams.addValue("line1",helperUtil.capitailizeWord(customerDetail.getAddress()));
        addressParams.addValue("districtId", customerDetail.getDistrict());
        addressParams.addValue("stateId",customerDetail.getState());
        Integer addressCount = this.namedParameterJdbcTemplate.update(HealthIdQuery.ADD_CUSTOMER_ADDRESS,addressParams,addKeyHolder);
        Integer addressId = addKeyHolder.getKey().intValue();
        //add healthId demographic details
        KeyHolder custKeyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource custParams = new MapSqlParameterSource();
        custParams.addValue("custId",customerId);
        custParams.addValue("firstName",helperUtil.capitailizeWord(customerDetail.getFirstName()));
        custParams.addValue("lastName",helperUtil.capitailizeWord(customerDetail.getLastName()));
        custParams.addValue("dob",formatter.parse(customerDetail.getDob()));
        custParams.addValue("relation",customerDetail.getRelationId());
        custParams.addValue("address",addressId);
        custParams.addValue("email",customerDetail.getEmailId());
        custParams.addValue("gender",customerDetail.getGender());
        custParams.addValue("healthId",customerDetail.getHealthId());
        custParams.addValue("healtIdNo", customerDetail.getHealthIdNo());
        custParams.addValue("token",customerDetail.getToken());
        try {
            Integer custCount = this.namedParameterJdbcTemplate.update(HealthIdQuery.ADD_HEALTH_ID_DEMOGRAPHIC, custParams, custKeyHolder);
            healthIdPk = custKeyHolder.getKey().intValue();
        }catch (DuplicateKeyException exception){
            logger.debug("Duplicate Key Exception :{}", exception.getMessage());
            healthIdPk = -1;
        }
        return healthIdPk;
    }

    @Override
    public Long addNewCustomer(Long mobile, int otp) throws Exception{
        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource custParams = new MapSqlParameterSource();
        custParams.addValue("firstName","Guest");
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
        addressParams.addValue("address",helperUtil.capitailizeWord(customerDetail.getAddress()));
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
        custParams.addValue("firstName",helperUtil.capitailizeWord(customerDetail.getFirstName()));
        custParams.addValue("lastName",helperUtil.capitailizeWord(customerDetail.getLastName()));
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        custParams.addValue("dob",formatter.parse(customerDetail.getDob()));
        custParams.addValue("relation",customerDetail.getRelationId());
        custParams.addValue("email",customerDetail.getEmailId());
        custParams.addValue("gender",customerDetail.getGender());
        custParams.addValue("profilePhoto",customerDetail.getProfilePhoto());
        Integer custCount  =  this.namedParameterJdbcTemplate.update(HealthIdQuery.UPDATE_PROFILE_CUSTOMER,custParams);
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
    public HealthId getByHealth(String healthId) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("healthId", healthId);
        try {
            HealthId healthIdResponse = this.namedParameterJdbcTemplate.queryForObject(HealthIdQuery.GET_BY_HEALTH_ID, params, new HealthId.HealthIdRowMapper());
            return healthIdResponse;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
}
