package com.pb.dp.dao;

public interface HealthQuery {

		String GET_CUST_HEALTH = "select c.id as customerId,c.mobile,h.firstName,h.middleName,h.lastName,h.dob,mr.name as relationship,h.email " +
				"as emailId ,h.gender,h.id as healthdbId,h.healthId,h.healtIdNo,h.profilePhoto as profilePhoto,a2.line1 as address,a2.stateId,a2.districtId,a2.pincode,ms.ndhm_name as state,md.ndhm_name as district," +
				"h.isKyc from customer c(nolock) inner join healthId h(nolock) on h.customerId=c.id inner join address a2 on a2.id=h.addressId " +
				"inner join m_district md on md.ndhm_id = a2.districtId inner join m_state ms on ms.ndhm_id = a2.stateId " +
				"inner join m_relation mr on mr.id = h.relation where c.id=? and c.mobile=? and h.isActive=1";
	String GET_OTP = "select c.id, c.otp from customer c(nolock) where c.mobile=?";
	String GET_CUST_HEALTH_PROFILE = "select top 1 c.id as customerId,c.mobile,h.firstName,h.middleName,h.lastName,h.dob,mr.name as relationship,h.email as emailId ,h.gender,h.id as healthdbId,h.healthId,h.healtIdNo,h.profilePhoto as profilePhoto,a2.line1 as address,a2.stateId,a2.districtId,a2.pincode,ms.ndhm_name as state,md.ndhm_name as district,h.isKyc,h.qrCode,h.healthCard from customer c(nolock) inner join healthId h(nolock) on h.customerId=c.id inner join address a2 on a2.id=h.addressId inner join m_district md on md.ndhm_id = a2.districtId inner join m_state ms on ms.ndhm_id = a2.stateId inner join m_relation mr on mr.id = h.relation where c.id=? and h.isActive=1 ORDER by h.createdAt DESC";
	String GET_HEALTH_TOKEN = "select healthIdToken from healthId (nolock) where healthId=?";
	String UPDATE_SMS_OTP_VERIFY = "UPDATE sms_otp_details SET IsVerify=1,VerifiedOn=GETDATE(),UpdatedOn=getDate() WHERE MobileNo=? and OTP=?";
	String UPDATE_QR_CODE = "UPDATE healthId SET qrCode=? WHERE healthId=?";
	String UPDATE_CARD_BYTE = "UPDATE healthId SET healthCard=? WHERE healthId=?";
	String GET_CUST_HEALTH_PROFILE_ID = "select c.id as customerId,c.mobile,h.firstName,h.middleName,h.lastName,h.dob,mr.name as relationship,h.email as emailId ,h.gender,h.id as healthdbId,h.healthId,h.healtIdNo,h.profilePhoto as profilePhoto,a2.line1 as address,a2.stateId as stateId,a2.districtId as districtId,a2.pincode,ms.ndhm_name as state,md.ndhm_name as district,h.isKyc,h.qrCode,h.healthCard from customer c(nolock) inner join healthId h(nolock) on h.customerId=c.id inner join address a2 on a2.id=h.addressId inner join m_district md on md.ndhm_id = a2.districtId inner join m_state ms on ms.ndhm_id = a2.stateId inner join m_relation mr on mr.id = h.relation where c.id=? and h.healthId =? and h.isActive=1";
	String GET_CUSTOMER_PROFILE = "select c.mobile, c.firstName,c.midName as middleName,c.lastName, c.relationship, c.gender from customer c(nolock) where c.id=?";
	String GET_CUSTOMER_PROFILE_HEALTH = "select top 1 c.mobile,h.firstName,h.middleName,h.lastName,mr.name as relationship,h.gender from customer c(nolock) inner join healthId h(nolock) on h.customerId=c.id inner join m_relation mr on mr.id = h.relation where c.id=? and h.isActive=1 ORDER by h.createdAt DESC";
	String DELETE_HEALTH_ID = "UPDATE healthId SET isActive=0 WHERE healthId=?";
}
