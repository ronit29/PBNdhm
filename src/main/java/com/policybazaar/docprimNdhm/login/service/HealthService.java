package com.policybazaar.docprimNdhm.login.service;

import java.util.List;

import com.policybazaar.docprimNdhm.login.model.CustomerHealth;

public interface HealthService {

	List<CustomerHealth> getCustHealthDetails(long mobileNo, int customerId);

}
