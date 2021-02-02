package com.pb.dp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pb.dp.dao.OtpDao;

@Service
public class OtpServiceImpl implements OtpService {

	
	@Autowired
	private OtpDao otpDao;
	
	@Override
	public boolean isVerified(int otp, Long mobileNo, int customerId) {
		return otpDao.isVerified(otp,mobileNo,customerId);
	}

}
