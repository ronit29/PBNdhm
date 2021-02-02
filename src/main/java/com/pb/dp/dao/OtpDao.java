package com.pb.dp.dao;

public interface OtpDao {

	boolean isVerified(int otp, Long mobileNo, int customerId);

	void insertTxnId(int customerId, String txnId);

}
