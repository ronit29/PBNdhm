package com.pb.dp.service;

public interface OtpService {

	int isVerified(int otp, Long mobileNo);

	String verifyNdhmOtp(String otp, String txnId) throws Exception;

}
