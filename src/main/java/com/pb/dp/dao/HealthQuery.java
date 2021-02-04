package com.pb.dp.dao;

public interface HealthQuery {

	String GET_CUST_HEALTH = "select c.id as customerId,c.mobile,c.firstName,c.midName,c.lastName,c.dob,c.relationship,c.emailId,c.gender,h.healthId,h.healtIdNo,c.address_id as addressId,h.isKyc from customer c(nolock) inner join healthId h(nolock) on h.customerId=c.id where c.id=? and c.mobile=? and h.isActive=1";
	String GET_OTP = "select c.id, c.otp from customer c(nolock) where c.mobile=?";
	String UPDATE_TXN_ID = "UPDATE DocprimeNDHM.dbo.customer SET txnId=? WHERE id=?";
	String GET_CUST_HEALTH_PROFILE = "select top 1 c.id as customerId,c.mobile,c.firstName,c.midName,c.lastName,c.dob,c.relationship,c.emailId,c.gender,h.healthId,h.healtIdNo,c.address_id as addressId,h.isKyc,h.qrCode,h.healthCard from customer c(nolock) inner join healthId h(nolock) on h.customerId=c.id where c.id=? and h.isActive=1 ORDER by h.createdAt DESC";
	String GET_HEALTH_TOKEN = "select healthIdToken from healthId (nolock) where healthId=?";
	String UPDATE_SMS_OTP_VERIFY = "UPDATE sms_otp_details SET IsVerify=1,VerifiedOn=GETDATE(),UpdatedOn=getDate() WHERE MobileNo=? and OTP=?";
	String UPDATE_QR_CODE = "UPDATE healthId SET qrCode=? WHERE healthId=?";
	String UPDATE_CARD_BYTE = "UPDATE healthId SET healthCard=? WHERE healthId=?";
	
}
