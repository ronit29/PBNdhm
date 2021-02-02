package com.pb.dp.service;

public interface OtpService {

	boolean isVerified(int otp, Long mobileNo, int customerId);

}
