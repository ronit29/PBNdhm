package com.policybazaar.docprimNdhm.login.dao;

public interface HealthQuery {

	String GET_CUST_HEALTH = "select c.id as customerId,c.mobile,c.firstName,c.midName,c.lastName,c.dob,c.relationship,c.emailId,c.gender,h.healthId,h.healtIdNo,c.address_id as addressId,h.isKyc from customer c(nolock) inner join healthId h(nolock) on h.customerId=c.id where c.id=? and c.mobile=? and h.isActive=1";
	
}
