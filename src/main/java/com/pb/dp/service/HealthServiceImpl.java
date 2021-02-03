package com.pb.dp.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pb.dp.dao.HealthDao;
import com.pb.dp.model.CustomerHealth;
import com.pb.dp.model.GetHealthProfileRequest;

@Service
public class HealthServiceImpl implements HealthService {

	@Autowired
	private HealthDao healthDao;
	
	@Override
	public List<CustomerHealth> getCustHealthDetails(long mobileNo, int customerId) {
		return healthDao.getCustHealthDetails(mobileNo,customerId);
	}

	@Override
	public CustomerHealth getHealthProfile(int customerId, GetHealthProfileRequest custHealthOtpRequest)
			throws Exception {
		CustomerHealth response = healthDao.getHealthProfile(customerId);
		if(null!=custHealthOtpRequest.getHealthId() && !custHealthOtpRequest.getHealthId().isEmpty()) {
			//TODO call the Apis 
		}
		return response;
	}

}
