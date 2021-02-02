package com.pb.dp.dao;

import java.util.List;

import com.pb.dp.model.CustomerHealth;

public interface HealthDao {

	List<CustomerHealth> getCustHealthDetails(long mobileNo, int customerId);

}
