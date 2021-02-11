package com.pb.dp.dao;

import java.util.List;
import java.util.Map;

import com.pb.dp.model.CustomerHealth;

public interface HealthDao {

	List<CustomerHealth> getCustHealthDetails(long mobileNo, int customerId);

	CustomerHealth getHealthProfile(int customerId, String healthId);

	String getHealthToken(String healthId);

	void updateQrCode(String qrCode, String healthId);

	void updateCard(String byteStringCard, String healthId);

	Map<String,Object> getCustomerProfile(int customerId);

	void updateHealth(CustomerHealth response);

}
