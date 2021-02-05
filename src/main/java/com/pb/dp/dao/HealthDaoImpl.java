package com.pb.dp.dao;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pb.dp.model.CustomerHealth;

@Repository
public class HealthDaoImpl implements HealthDao {

	@Autowired
    private DataSource dataSource;

    private JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void setJdbcTemplate() {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }
	
	@Override
	public List<CustomerHealth> getCustHealthDetails(long mobileNo, int customerId) {
		return jdbcTemplate.query(HealthQuery.GET_CUST_HEALTH, new CustomerHealth.CustomerHealthMapper(),customerId,mobileNo);
	}

	@Override
	public CustomerHealth getHealthProfile(int customerId,String healthId) {
		Map<String,Object> response = null;
		CustomerHealth healthReponse = new CustomerHealth();
		try {
			if(null != healthId && !healthId.isEmpty()) {
				response = jdbcTemplate.queryForMap(HealthQuery.GET_CUST_HEALTH_PROFILE_ID,customerId,healthId);
			}else {
				response = jdbcTemplate.queryForMap(HealthQuery.GET_CUST_HEALTH_PROFILE,customerId);
			}
			ObjectMapper mapper = new ObjectMapper();
			healthReponse = mapper.convertValue(response, CustomerHealth.class);
			
		}catch (EmptyResultDataAccessException e) {
		}
		return healthReponse;
	}
	
	@Override
	public String getHealthToken(String healthId) {
		String authToken = null;
		try {
			authToken = jdbcTemplate.queryForObject(HealthQuery.GET_HEALTH_TOKEN,String.class,healthId);
		}catch (EmptyResultDataAccessException e) {
		}
		return authToken;
	}

	@Override
	public void updateQrCode(String qrCode,String healthId) {
		jdbcTemplate.update(HealthQuery.UPDATE_QR_CODE, qrCode,healthId);
		
	}

	@Override
	public void updateCard(String byteStringCard, String healthId) {
		jdbcTemplate.update(HealthQuery.UPDATE_CARD_BYTE, byteStringCard,healthId);
		
	}

}
