package com.pb.dp.dao;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

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

}