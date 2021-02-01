package com.policybazaar.docprimNdhm.master.dao.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.policybazaar.docprimNdhm.master.dao.MasterDao;

/**
 * This class implements dao layer for master
 * 
 * @author Aditya Rathore
 *
 */
@Repository("masterDao")
public class MasterDaoImpl implements MasterDao {

	@Autowired
	private DataSource dataSource;
	
	private JdbcTemplate jdbcTemplate;
	
	@PostConstruct
	public void init() {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	private static final String GET_ALL_STATES = "SELECT ndhm_id AS id, ndhm_name AS name FROM dbo.m_state s(nolock) WHERE isActive=1 ORDER BY ndhm_name ASC";
	private static final String GET_ALL_DISTRICTS_FOR_STATE = "SELECT District_Code AS id, District_Name AS name FROM dbo.m_state_district_code msdc (nolock) WHERE State_Code = ?";
	
	@Override
	public List<Map<String,Object>> getState() throws Exception{
		return jdbcTemplate.queryForList(GET_ALL_STATES);
	}

	@Override
	public List<Map<String, Object>> getDistrictsForState(Integer stateCode) throws Exception {
		return jdbcTemplate.queryForList(GET_ALL_DISTRICTS_FOR_STATE,stateCode);
	}
	
}
