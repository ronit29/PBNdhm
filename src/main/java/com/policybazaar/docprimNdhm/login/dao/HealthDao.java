package com.policybazaar.docprimNdhm.login.dao;

import java.util.List;

import com.policybazaar.docprimNdhm.login.model.CustomerHealth;

public interface HealthDao {

	List<CustomerHealth> getCustHealthDetails(long mobileNo, int customerId);

}
