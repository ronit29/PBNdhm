package com.pb.dp.model;

public class VerifyOtpWithAadharRequest {
	
	private String otp;
	private String restrictions;
	private String txnId;
	private String healthId;
	
	public String getHealthId() {
		return healthId;
	}
	public void setHealthId(String healthId) {
		this.healthId = healthId;
	}
	public String getOtp() {
		return otp;
	}
	public void setOtp(String otp) {
		this.otp = otp;
	}
	public String getRestrictions() {
		return restrictions;
	}
	public void setRestrictions(String restrictions) {
		this.restrictions = restrictions;
	}
	public String getTxnId() {
		return txnId;
	}
	public void setTxnId(String txnId) {
		this.txnId = txnId;
	}


}
