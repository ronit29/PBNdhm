package com.pb.dp.healthIdCreation.model;


import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Address {

  private long id;
  private String line1;
  private String line2;
  private long districtId;
  private long stateId;
  private java.sql.Timestamp createdAt;
  private long createdBy;
  private long updatedBy;
  private java.sql.Timestamp updatedAt;


  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }


  public String getLine1() {
    return line1;
  }

  public void setLine1(String line1) {
    this.line1 = line1;
  }


  public String getLine2() {
    return line2;
  }

  public void setLine2(String line2) {
    this.line2 = line2;
  }


  public long getDistrictId() {
    return districtId;
  }

  public void setDistrictId(long districtId) {
    this.districtId = districtId;
  }


  public long getStateId() {
    return stateId;
  }

  public void setStateId(long stateId) {
    this.stateId = stateId;
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

  @Override
  public String toString() {
    return "Address{" +
            "id=" + id +
            ", line1='" + line1 + '\'' +
            ", line2='" + line2 + '\'' +
            ", districtId=" + districtId +
            ", stateId=" + stateId +
            ", createdAt=" + createdAt +
            ", createdBy=" + createdBy +
            ", updatedBy=" + updatedBy +
            ", updatedAt=" + updatedAt +
            '}';
  }

  public static class AddressRowMapper implements RowMapper<Address> {
    @Override
    public Address mapRow(ResultSet rs, int rownum) throws SQLException {
      Address address = new Address();
      address.setId(rs.getInt("id"));
      address.setLine1(rs.getString("line1"));
      address.setLine2(rs.getString("line2"));
      address.setDistrictId(rs.getLong("districtId"));
      address.setStateId(rs.getLong("stateId"));
      address.setCreatedAt(rs.getTimestamp("createdAt"));
      address.setCreatedBy(rs.getLong("createdBy"));
      address.setUpdatedAt(rs.getTimestamp("updatedAt"));
      address.setUpdatedBy(rs.getLong("updatedBy"));
      return address;
    }
  }
}
