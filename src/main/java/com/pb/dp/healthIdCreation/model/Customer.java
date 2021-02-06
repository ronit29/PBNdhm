package com.pb.dp.healthIdCreation.model;


import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class Customer {

  private long id;
  private String firstName;
  private String midName;
  private String lastName;
  private java.sql.Date dob;
  private String relationship;
  private long addressId;
  private String emailId;
  private long mobile;
  private String gender;
  private String healthId;
  private String isActive;
  private Timestamp createdAt;
  private long createdBy;
  private long updatedBy;
  private Timestamp updatedAt;
  private long otp;
  private java.sql.Timestamp otpCreatedAt;
private String txnId;
private String token;
private Integer ndhmOtpType;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
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


  public java.sql.Date getDob() {
    return dob;
  }

  public void setDob(java.sql.Date dob) {
    this.dob = dob;
  }


  public String getRelationship() {
    return relationship;
  }

  public void setRelationship(String relationship) {
    this.relationship = relationship;
  }


  public long getAddressId() {
    return addressId;
  }

  public void setAddressId(long addressId) {
    this.addressId = addressId;
  }


  public String getEmailId() {
    return emailId;
  }

  public void setEmailId(String emailId) {
    this.emailId = emailId;
  }


  public long getMobile() {
    return mobile;
  }

  public void setMobile(long mobile) {
    this.mobile = mobile;
  }


  public String getGender() {
    return gender;
  }

  public void setGender(String gender) {
    this.gender = gender;
  }


  public String getIsActive() {
    return isActive;
  }

  public void setIsActive(String isActive) {
    this.isActive = isActive;
  }


  public java.sql.Timestamp getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(java.sql.Timestamp createdAt) {
    this.createdAt = createdAt;
  }


  public long getCreatedBy() {
    return createdBy;
  }

  public void setCreatedBy(long createdBy) {
    this.createdBy = createdBy;
  }


  public long getUpdatedBy() {
    return updatedBy;
  }

  public void setUpdatedBy(long updatedBy) {
    this.updatedBy = updatedBy;
  }


  public java.sql.Timestamp getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(java.sql.Timestamp updatedAt) {
    this.updatedAt = updatedAt;
  }


  public long getOtp() {
    return otp;
  }

  public void setOtp(long otp) {
    this.otp = otp;
  }


  public java.sql.Timestamp getOtpCreatedAt() {
    return otpCreatedAt;
  }

  public void setOtpCreatedAt(java.sql.Timestamp otpCreatedAt) {
    this.otpCreatedAt = otpCreatedAt;
  }

  public String getHealthId() {
    return healthId;
  }

  public void setHealthId(String healthId) {
    this.healthId = healthId;
  }

  public String getTxnId() {
    return txnId;
  }

  public void setTxnId(String txnId) {
    this.txnId = txnId;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public Integer getNdhmOtpType() {
    return ndhmOtpType;
  }

  public void setNdhmOtpType(Integer ndhmOtpType) {
    this.ndhmOtpType = ndhmOtpType;
  }

  @Override
  public String toString() {
    return "Customer{" +
            "id=" + id +
            ", firstName='" + firstName + '\'' +
            ", midName='" + midName + '\'' +
            ", lastName='" + lastName + '\'' +
            ", dob=" + dob +
            ", relationship='" + relationship + '\'' +
            ", addressId=" + addressId +
            ", emailId='" + emailId + '\'' +
            ", mobile=" + mobile +
            ", gender='" + gender + '\'' +
            ", healthId='" + healthId + '\'' +
            ", isActive='" + isActive + '\'' +
            ", createdAt=" + createdAt +
            ", createdBy=" + createdBy +
            ", updatedBy=" + updatedBy +
            ", updatedAt=" + updatedAt +
            ", otp=" + otp +
            ", otpCreatedAt=" + otpCreatedAt +
            ", txnId='" + txnId + '\'' +
            ", token='" + token + '\'' +
            ", ndhmOtpType=" + ndhmOtpType +
            '}';
  }

  public static class CustomerRowMapper implements RowMapper<Customer> {
    @Override
    public Customer mapRow(ResultSet rs, int rownum) throws SQLException {
      Customer customer = new Customer();
      customer.setId(rs.getInt("id"));
      customer.setFirstName(rs.getString("firstName"));
      customer.setLastName(rs.getString("lastName"));
      customer.setDob(rs.getDate("dob"));
      customer.setRelationship(rs.getString("relationship"));
      customer.setMidName(rs.getString("midName"));
      customer.setMobile(rs.getLong("mobile"));
      customer.setGender(rs.getString("gender"));
      customer.setHealthId(rs.getString("healthId"));
      customer.setEmailId(rs.getString("emailId"));
      customer.setIsActive(rs.getString("isActive"));
      customer.setCreatedAt(rs.getTimestamp("createdAt"));
      customer.setCreatedBy(rs.getLong("createdBy"));
      customer.setUpdatedAt(rs.getTimestamp("updatedAt"));
      customer.setUpdatedBy(rs.getLong("updatedBy"));
      customer.setOtp(rs.getLong("otp"));
      customer.setOtpCreatedAt(rs.getTimestamp("otpCreatedAt"));
      customer.setTxnId(rs.getString("txnId"));
      customer.setToken(rs.getString("token"));
      customer.setNdhmOtpType(rs.getInt("ndhmOtpType"));
      customer.setAddressId(rs.getLong("address_id"));
      return customer;
    }
  }
}
