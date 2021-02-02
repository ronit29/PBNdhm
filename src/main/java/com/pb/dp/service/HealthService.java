package com.pb.dp.service;

import java.util.List;

import com.pb.dp.model.CustomerHealth;

public interface HealthService {

	List<CustomerHealth> getCustHealthDetails(long mobileNo, int customerId);

}
