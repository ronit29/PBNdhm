package com.pb.dp.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pb.dp.dao.HealthDao;
import com.pb.dp.model.CustomerHealth;

@Service
public class HealthServiceImpl implements HealthService {

	@Autowired
	private HealthDao healthDao;
	
	@Override
	public List<CustomerHealth> getCustHealthDetails(long mobileNo, int customerId) {
		return healthDao.getCustHealthDetails(mobileNo,customerId);
	}

}
