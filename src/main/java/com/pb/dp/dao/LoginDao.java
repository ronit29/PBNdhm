package com.pb.dp.dao;

public interface LoginDao {

    Integer inserOtpDetails(int otp, Integer countryCode, Long mobile, String message, String smsResponse, int smsType, String uuid);

	String getCustomerName(Long mobile);
}
