package com.pb.dp.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import org.springframework.jdbc.core.RowMapper;

public class CustomerHealth {
	
	private int customerId;
	private long mobile;
	private String firstName;
	private String middleName;
	private String lastName;
	private Date dob;
	private String dobStr;
	private String relationship;
	private String emailId;
	private String gender;
	private String healthId;
	private String healtIdNo;
	private int addressId;
	private String address;
	private String state;
	private Long stateId;
	private String district;
	private Long districtId;
	private short isKyc;
	private String qrCode;
	private String healthCard;
	private String txnId;
	private int healthdbId;
	private Integer pincode;
	private String profilePhoto;
	
	public int getCustomerId() {
		return customerId;
	}
	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}
	public long getMobile() {
		return mobile;
	}
	public void setMobile(long mobile) {
		this.mobile = mobile;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getMiddleName() {
		return middleName;
	}
	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public Date getDob() {
		return dob;
	}
	public void setDob(Date dob) {
		this.dob = dob;
	}
	
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getDistrict() {
		return district;
	}
	public void setDistrict(String district) {
		this.district = district;
	}
	public String getRelationship() {
		return relationship;
	}
	public void setRelationship(String relationship) {
		this.relationship = relationship;
	}
	public String getEmailId() {
		return emailId;
	}
	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	
	public int getHealthdbId() {
		return healthdbId;
	}
	public void setHealthdbId(int healthdbId) {
		this.healthdbId = healthdbId;
	}
	public String getHealtIdNo() {
		return healtIdNo;
	}
	
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getHealthId() {
		return healthId;
	}
	public void setHealthId(String healthId) {
		this.healthId = healthId;
	}
	public void setHealtIdNo(String healtIdNo) {
		this.healtIdNo = healtIdNo;
	}
	public int getAddressId() {
		return addressId;
	}
	public void setAddressId(int addressId) {
		this.addressId = addressId;
	}
	public short getIsKyc() {
		return isKyc;
	}
	public void setIsKyc(short isKyc) {
		this.isKyc = isKyc;
	}
	
	public String getQrCode() {
		return qrCode;
	}
	public void setQrCode(String qrCode) {
		this.qrCode = qrCode;
	}
	public String getHealthCard() {
		return healthCard;
	}
	public void setHealthCard(String healthCard) {
		this.healthCard = healthCard;
	}
	
	public String getTxnId() {
		return txnId;
	}
	public void setTxnId(String txnId) {
		this.txnId = txnId;
	}

	public String getDobStr() {
		return dobStr;
	}

	public void setDobStr(String dobStr) {
		this.dobStr = dobStr;
	}

	public Long getStateId() {
		return stateId;
	}

	public void setStateId(Long stateId) {
		this.stateId = stateId;
	}

	public Long getDistrictId() {
		return districtId;
	}

	public void setDistrictId(Long districtId) {
		this.districtId = districtId;
	}

	public Integer getPincode() {
		return pincode;
	}

	public void setPincode(Integer pincode) {
		this.pincode = pincode;
	}

	public String getProfilePhoto() {
		return profilePhoto;
	}

	public void setProfilePhoto(String profilePhoto) {
		this.profilePhoto = profilePhoto;
	}

	public static class CustomerHealthMapper implements RowMapper<CustomerHealth> {
        @Override
        public CustomerHealth mapRow(ResultSet rs, int rowNum) throws SQLException {
        	CustomerHealth customerHealth = new CustomerHealth();
            customerHealth.setCustomerId(rs.getInt("customerId"));
            customerHealth.setMobile(rs.getLong("mobile"));
            customerHealth.setFirstName(rs.getString("firstName"));
            customerHealth.setMiddleName(rs.getString("middleName"));
            customerHealth.setLastName(rs.getString("lastName"));
            customerHealth.setDob(rs.getDate("dob"));
            customerHealth.setRelationship(rs.getString("relationship"));
            customerHealth.setEmailId(rs.getString("emailId"));
            customerHealth.setGender(rs.getString("gender"));
            customerHealth.setHealthdbId(rs.getInt("healthdbId"));
            customerHealth.setHealthId(rs.getString("healthId")); 
            customerHealth.setHealtIdNo(rs.getString("healtIdNo"));
            customerHealth.setAddress(rs.getString("address"));
            customerHealth.setIsKyc(rs.getShort("isKyc"));
            customerHealth.setState(rs.getString("state"));
            customerHealth.setDistrict(rs.getString("district"));
            customerHealth.setStateId(rs.getLong("stateId"));
            customerHealth.setDistrictId(rs.getLong("districtId"));
            customerHealth.setPincode(rs.getInt("pincode"));
			customerHealth.setProfilePhoto(rs.getString("profilePhoto"));
            return customerHealth;
        }
    }
}
