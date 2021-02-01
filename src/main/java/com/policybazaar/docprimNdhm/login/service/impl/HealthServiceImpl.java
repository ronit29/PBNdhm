package com.policybazaar.docprimNdhm.login.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.policybazaar.docprimNdhm.login.dao.HealthDao;
import com.policybazaar.docprimNdhm.login.model.CustomerHealth;
import com.policybazaar.docprimNdhm.login.service.HealthService;

@Service
public class HealthServiceImpl implements HealthService {

	@Autowired
	private HealthDao healthDao;
	
	@Override
	public List<CustomerHealth> getCustHealthDetails(long mobileNo, int customerId) {
		return healthDao.getCustHealthDetails(mobileNo,customerId);
	}

}
