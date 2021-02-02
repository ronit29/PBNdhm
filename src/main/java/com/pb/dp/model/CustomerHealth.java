package com.pb.dp.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import org.springframework.jdbc.core.RowMapper;

public class CustomerHealth {
	
	private int customerId;
	private long mobile;
	private String firstName;
	private String midName;
	private String lastName;
	private Date dob;
	private String relationship;
	private String emailId;
	private String gender;
	private String healthId;
	private String healtIdNo;
	private int addressId;
	private short isKyc;
	
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
	public String getMidName() {
		return midName;
	}
	public void setMidName(String midName) {
		this.midName = midName;
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
	public String getHealtIdNo() {
		return healtIdNo;
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
	
	public static class CustomerHealthMapper implements RowMapper<CustomerHealth> {
        @Override
        public CustomerHealth mapRow(ResultSet rs, int rowNum) throws SQLException {
        	CustomerHealth customerHealth = new CustomerHealth();
            customerHealth.setCustomerId(rs.getInt("customerId"));
            customerHealth.setMobile(rs.getLong("mobile"));
            customerHealth.setFirstName(rs.getString("firstName"));
            customerHealth.setMidName(rs.getString("midName"));
            customerHealth.setLastName(rs.getString("lastName"));
            customerHealth.setDob(rs.getDate("dob"));
            customerHealth.setRelationship(rs.getString("relationship"));
            customerHealth.setEmailId(rs.getString("emailId"));
            customerHealth.setGender(rs.getString("gender"));
            customerHealth.setHealthId(rs.getString("healthId")); 
            customerHealth.setHealtIdNo(rs.getString("healtIdNo"));
            customerHealth.setAddressId(rs.getInt("addressId"));
            customerHealth.setIsKyc(rs.getShort("isKyc"));
            return customerHealth;
        }
    }
}