package com.pb.dp.service;

import java.util.List;
import java.util.Map;

import com.pb.dp.model.CustomerHealth;
import com.pb.dp.model.GetHealthProfileRequest;

public interface HealthService {

	List<CustomerHealth> getCustHealthDetails(long mobileNo, int customerId);

	CustomerHealth getHealthProfile(int customerId, GetHealthProfileRequest custHealthOtpRequest) throws Exception;

	String getCardContent(int customerId, GetHealthProfileRequest custHealthOtpRequest) throws Exception;

	Map<String, Object> getCustomerProfile(int customerId);

}
