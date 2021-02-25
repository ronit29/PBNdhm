package com.pb.dp.model;


import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class HealthId {

  private long id;
  private String healthId;
  private String healtIdNo;
  private long customerId;
  private String isActive;
  private java.sql.Timestamp createdAt;
  private long createdBy;
  private long updatedBy;
  private java.sql.Timestamp updatedAt;
  private long isKyc;
  private java.sql.Timestamp kycAt;
  private String healthIdToken;
  private String qrCode;
  private String healthCard;
  private long relation;
  private String firstName;
  private String lastName;
  private long addressId;
  private String email;
  private java.sql.Date dob;
  private String gender;
  private String middleName;


  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }


  public String getHealthId() {
    return healthId;
  }

  public void setHealthId(String healthId) {
    this.healthId = healthId;
  }


  public String getHealtIdNo() {
    return healtIdNo;
  }

  public void setHealtIdNo(String healtIdNo) {
    this.healtIdNo = healtIdNo;
  }


  public long getCustomerId() {
    return customerId;
  }

  public void setCustomerId(long customerId) {
    this.customerId = customerId;
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


  public long getIsKyc() {
    return isKyc;
  }

  public void setIsKyc(long isKyc) {
    this.isKyc = isKyc;
  }


  public java.sql.Timestamp getKycAt() {
    return kycAt;
  }

  public void setKycAt(java.sql.Timestamp kycAt) {
    this.kycAt = kycAt;
  }


  public String getHealthIdToken() {
    return healthIdToken;
  }

  public void setHealthIdToken(String healthIdToken) {
    this.healthIdToken = healthIdToken;
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


  public long getRelation() {
    return relation;
  }

  public void setRelation(long relation) {
    this.relation = relation;
  }


  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }


  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }


  public long getAddressId() {
    return addressId;
  }

  public void setAddressId(long addressId) {
    this.addressId = addressId;
  }


  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }


  public java.sql.Date getDob() {
    return dob;
  }

  public void setDob(java.sql.Date dob) {
    this.dob = dob;
  }


  public String getGender() {
    return gender;
  }

  public void setGender(String gender) {
    this.gender = gender;
  }


  public String getMiddleName() {
    return middleName;
  }

  public void setMiddleName(String middleName) {
    this.middleName = middleName;
  }

  @Override
  public String toString() {
    return "HealthId{" +
            "id=" + id +
            ", healthId='" + healthId + '\'' +
            ", healtIdNo='" + healtIdNo + '\'' +
            ", customerId=" + customerId +
            ", isActive='" + isActive + '\'' +
            ", createdAt=" + createdAt +
            ", createdBy=" + createdBy +
            ", updatedBy=" + updatedBy +
            ", updatedAt=" + updatedAt +
            ", isKyc=" + isKyc +
            ", kycAt=" + kycAt +
            ", healthIdToken='" + healthIdToken + '\'' +
            ", qrCode='" + qrCode + '\'' +
            ", healthCard='" + healthCard + '\'' +
            ", relation=" + relation +
            ", firstName='" + firstName + '\'' +
            ", lastName='" + lastName + '\'' +
            ", addressId=" + addressId +
            ", email='" + email + '\'' +
            ", dob=" + dob +
            ", gender='" + gender + '\'' +
            ", middleName='" + middleName + '\'' +
            '}';
  }

  public static class HealthIdRowMapper implements RowMapper<HealthId> {
    @Override
    public HealthId mapRow(ResultSet rs, int rownum) throws SQLException {
      HealthId healthId = new HealthId();
      healthId.setId(rs.getLong("id"));
      healthId.setHealthId(rs.getString("healthId"));
      healthId.setHealtIdNo(rs.getString("healtIdNo"));
      healthId.setHealthIdToken(rs.getString("healthIdToken"));
      healthId.setQrCode(rs.getString("qrCode"));
      healthId.setHealthCard(rs.getString("healthCard"));
      healthId.setCustomerId(rs.getLong("customerId"));
      healthId.setFirstName(rs.getString("firstName"));
      healthId.setLastName(rs.getString("lastName"));
      healthId.setDob(rs.getDate("dob"));
      healthId.setRelation(rs.getInt("relation"));
      healthId.setMiddleName(rs.getString("middleName"));
      healthId.setGender(rs.getString("gender"));
      healthId.setHealthId(rs.getString("healthId"));
      healthId.setEmail(rs.getString("email"));
      healthId.setKycAt(rs.getTimestamp("kycAt"));
      healthId.setIsKyc(rs.getInt("isKyc"));
      healthId.setIsActive(rs.getString("isActive"));
      healthId.setCreatedAt(rs.getTimestamp("createdAt"));
      healthId.setCreatedBy(rs.getLong("createdBy"));
      healthId.setUpdatedAt(rs.getTimestamp("updatedAt"));
      healthId.setUpdatedBy(rs.getLong("updatedBy"));
      healthId.setAddressId(rs.getLong("addressId"));
      return healthId;
    }
  }

}
