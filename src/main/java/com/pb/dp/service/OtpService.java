package com.pb.dp.service;

public interface OtpService {

	boolean isVerified(int otp, Long mobileNo);

	String sendNdhmOtp(long mobileNo, int customerId) throws Exception;

	String verifyNdhmOtp(String otp, String txnId) throws Exception;

}
