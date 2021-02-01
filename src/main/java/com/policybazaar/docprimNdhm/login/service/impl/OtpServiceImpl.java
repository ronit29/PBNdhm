package com.policybazaar.docprimNdhm.login.service.impl;

import org.springframework.stereotype.Service;

import com.policybazaar.docprimNdhm.login.service.OtpService;

@Service
public class OtpServiceImpl implements OtpService {

	@Override
	public boolean isVerified(int otp, Long mobileNo, int customerId) {
		return otp==1234;
	}

}
