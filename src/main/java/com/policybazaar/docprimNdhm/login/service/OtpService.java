package com.policybazaar.docprimNdhm.login.service;

public interface OtpService {

	boolean isVerified(int otp, Long mobileNo, int customerId);

}
