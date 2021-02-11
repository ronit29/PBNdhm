package com.pb.dp.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pb.dp.model.CustomerHealth;

@Repository
public class HealthDaoImpl implements HealthDao {

	@Autowired
	private DataSource dataSource;
	private SimpleJdbcCall updateCustomerHealth;

	private JdbcTemplate jdbcTemplate;

	@PostConstruct
	public void setJdbcTemplate() {
		this.jdbcTemplate = new JdbcTemplate(this.dataSource);
		updateCustomerHealth = new SimpleJdbcCall(this.dataSource).withSchemaName("dbo")
				.withProcedureName("updateCustomerHealth");
	}

	@Override
	public List<CustomerHealth> getCustHealthDetails(long mobileNo, int customerId) {
		return jdbcTemplate.query(HealthQuery.GET_CUST_HEALTH, new CustomerHealth.CustomerHealthMapper(), customerId,
				mobileNo);
	}

	@Override
	public CustomerHealth getHealthProfile(int customerId, String healthId) {
		Map<String, Object> response = null;
		CustomerHealth healthReponse = null;
		try {
			if (null != healthId && !healthId.isEmpty()) {
				response = jdbcTemplate.queryForMap(HealthQuery.GET_CUST_HEALTH_PROFILE_ID, customerId, healthId);
			} else {
				response = jdbcTemplate.queryForMap(HealthQuery.GET_CUST_HEALTH_PROFILE, customerId);
			}
			if (null != response) {
				ObjectMapper mapper = new ObjectMapper();
				healthReponse = new CustomerHealth();
				healthReponse = mapper.convertValue(response, CustomerHealth.class);
			}

		} catch (EmptyResultDataAccessException e) {
		}
		return healthReponse;
	}

	@Override
	public String getHealthToken(String healthId) {
		String authToken = null;
		try {
			authToken = jdbcTemplate.queryForObject(HealthQuery.GET_HEALTH_TOKEN, String.class, healthId);
		} catch (EmptyResultDataAccessException e) {
		}
		return authToken;
	}

	@Override
	public void updateQrCode(String qrCode, String healthId) {
		jdbcTemplate.update(HealthQuery.UPDATE_QR_CODE, qrCode, healthId);

	}

	@Override
	public void updateCard(String byteStringCard, String healthId) {
		jdbcTemplate.update(HealthQuery.UPDATE_CARD_BYTE, byteStringCard, healthId);

	}

	@Override
	public Map<String, Object> getCustomerProfile(int customerId) {
		Map<String, Object> response = null;
		try {
			response = jdbcTemplate.queryForMap(HealthQuery.GET_CUSTOMER_PROFILE, customerId);
			if (response.get("firstName") == null && response.get("middleName") == null
					&& response.get("lastName") == null) {
				response = jdbcTemplate.queryForMap(HealthQuery.GET_CUSTOMER_PROFILE_HEALTH, customerId);
			}
		} catch (EmptyResultDataAccessException e) {
			response = new HashMap<>();
		}
		return response;
	}

	@Override
	public void updateHealth(CustomerHealth response) {
		 Map<String, Object> callParams = new HashMap<String, Object>();
	     callParams.put("address",response.getCustomerId());
	     callParams.put("stateId",response.getStateId());
	     callParams.put("districtId",response.getDistrictId());
	     callParams.put("healthId",response.getHealthId());
	     callParams.put("healthIdNumber",response.getHealtIdNo());
	     callParams.put("email",response.getEmailId());
	     callParams.put("gender",response.getGender());
	     callParams.put("firstName",response.getFirstName());
	     callParams.put("middleName",response.getMiddleName());
	     callParams.put("lastName",response.getLastName());
	     callParams.put("isKyc",response.getIsKyc());
	     callParams.put("dob",response.getDobStr());
	     SqlParameterSource inParams = new MapSqlParameterSource(callParams);
	     updateCustomerHealth.execute(inParams);
		
	}

}
