package com.pb.dp.dao;

public interface OtpDao {

	int isVerified(int otp, Long mobileNo);

	void updateVerify(int otp, Long mobileNo);

}
