package com.pb.dp.dao;

public interface HealthQuery {

	String GET_CUST_HEALTH = "select c.id as customerId,c.mobile,c.firstName,c.midName,c.lastName,c.dob,c.relationship,c.emailId,c.gender,h.healthId,h.healtIdNo,c.address_id as addressId,h.isKyc from customer c(nolock) inner join healthId h(nolock) on h.customerId=c.id where c.id=? and c.mobile=? and h.isActive=1";
	String GET_OTP = "select c.otp from customer c(nolock) where c.id=? and c.mobile=?";
	String UPDATE_TXN_ID = "UPDATE DocprimeNDHM.dbo.customer SET txnId=? WHERE id=?";
	
}
