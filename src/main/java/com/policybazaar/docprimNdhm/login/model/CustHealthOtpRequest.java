package com.policybazaar.docprimNdhm.login.model;

public class CustHealthOtpRequest {

	private long mobileNo;
	private int otp;
	public long getMobileNo() {
		return mobileNo;
	}
	public void setMobileNo(long mobileNo) {
		this.mobileNo = mobileNo;
	}
	public int getOtp() {
		return otp;
	}
	public void setOtp(int otp) {
		this.otp = otp;
	}
}
