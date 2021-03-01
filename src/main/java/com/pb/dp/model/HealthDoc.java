package com.pb.dp.model;


import org.springframework.data.relational.core.sql.In;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class HealthDoc {

  private Long id;
  private Long customerId;
  private Long healthId;
  private String docName;
  private String docOwner;
  private Integer docTypeId;
  private String docS3Url;
  private String docTags;
  private String medicEntityName;
  private String doctorName;
  private java.sql.Timestamp createdAt;
  private Integer createdBy;
  private java.sql.Timestamp updatedAt;
  private Integer updatedBy;
  private String isActive;


  public long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }


  public long getCustomerId() {
    return customerId;
  }

  public void setCustomerId(Long customerId) {
    this.customerId = customerId;
  }


  public long getHealthId() {
    return healthId;
  }

  public void setHealthId(Long healthId) {
    this.healthId = healthId;
  }


  public String getDocName() {
    return docName;
  }

  public void setDocName(String docName) {
    this.docName = docName;
  }


  public String getDocOwner() {
    return docOwner;
  }

  public void setDocOwner(String docOwner) {
    this.docOwner = docOwner;
  }


  public Integer getDocTypeId() {
    return docTypeId;
  }

  public void setDocTypeId(Integer docTypeId) {
    this.docTypeId = docTypeId;
  }


  public String getDocS3Url() {
    return docS3Url;
  }

  public void setDocS3Url(String docS3Url) {
    this.docS3Url = docS3Url;
  }


  public String getDocTags() {
    return docTags;
  }

  public void setDocTags(String docTags) {
    this.docTags = docTags;
  }


  public String getMedicEntityName() {
    return medicEntityName;
  }

  public void setMedicEntityName(String medicEntityName) {
    this.medicEntityName = medicEntityName;
  }


  public String getDoctorName() {
    return doctorName;
  }

  public void setDoctorName(String doctorName) {
    this.doctorName = doctorName;
  }

  public Timestamp getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Timestamp createdAt) {
    this.createdAt = createdAt;
  }

  public Integer getCreatedBy() {
    return createdBy;
  }

  public void setCreatedBy(Integer createdBy) {
    this.createdBy = createdBy;
  }

  public Timestamp getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(Timestamp updatedAt) {
    this.updatedAt = updatedAt;
  }

  public Integer getUpdatedBy() {
    return updatedBy;
  }

  public void setUpdatedBy(Integer updatedBy) {
    this.updatedBy = updatedBy;
  }

  public String getIsActive() {
    return isActive;
  }

  public void setIsActive(String isActive) {
    this.isActive = isActive;
  }

  public static class HealthDocRowMapper implements RowMapper<HealthDoc> {
    @Override
    public HealthDoc mapRow(ResultSet rs, int rownum) throws SQLException {
      HealthDoc healthDoc = new HealthDoc();
      healthDoc.setId(rs.getLong("id"));
      healthDoc.setCustomerId(rs.getLong("healthDoc"));
      healthDoc.setHealthId(rs.getLong("dpNdhm"));

      healthDoc.setDocName(rs.getString("docName"));
      healthDoc.setDocTypeId(rs.getInt("docTypeId"));
      healthDoc.setDocOwner(rs.getString("docOwner"));
      healthDoc.setDocS3Url(rs.getString("docS3Url"));
      healthDoc.setDocTags(rs.getString("docTags"));
      healthDoc.setMedicEntityName(rs.getString("medicEntityName"));
      healthDoc.setDoctorName(rs.getString("doctorName"));
      healthDoc.setIsActive(rs.getString("isActive"));
      healthDoc.setCreatedAt(rs.getTimestamp("createdAt"));
      healthDoc.setCreatedBy(rs.getInt("createdBy"));
      healthDoc.setUpdatedAt(rs.getTimestamp("updatedAt"));
      healthDoc.setUpdatedBy(rs.getInt("updatedBy"));
      return healthDoc;
    }
  }
  
}
