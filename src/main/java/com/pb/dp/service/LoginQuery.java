package com.pb.dp.service;

public interface LoginQuery {

    String INSERT_OTP_DETAILS = "INSERT INTO DocprimeNDHM.dbo.sms_otp_details (CountryCode, MobileNo, OTP, Message, Response, SMSType, UUID) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?)";
	String GET_CUST_NAME = "select c.firstName from customer c(nolock) where c.mobile=?";
}
