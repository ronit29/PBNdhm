package com.pb.dp.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.data.relational.core.sql.In;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class HealthDoc{
    private long id;
    private String healthId;
    private long customerId;

    private String isActive;
    private Date createdAt;
    private long createdBy;
    private long updatedBy;
    private Date updatedAt;

    private String docName;
    private String docOwner;
    private int docTypeId;
    private String docS3Url;
    private String docTags;
    private String medicEntityName;
    private String doctorName;

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

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
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

    public int getDocTypeId() {
        return docTypeId;
    }

    public void setDocTypeId(int docTypeId) {
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

    @Override
    public String toString() {
        return "HealthDoc{" +
                "id=" + id +
                ", healthId='" + healthId + '\'' +
                ", customerId=" + customerId +
                ", isActive='" + isActive + '\'' +
                ", createdAt=" + createdAt +
                ", createdBy=" + createdBy +
                ", updatedBy=" + updatedBy +
                ", updatedAt=" + updatedAt +
                ", docName='" + docName + '\'' +
                ", docOwner='" + docOwner + '\'' +
                ", docTypeId=" + docTypeId +
                ", docS3Url='" + docS3Url + '\'' +
                ", docTags='" + docTags + '\'' +
                ", medicEntityName='" + medicEntityName + '\'' +
                ", doctorName='" + doctorName + '\'' +
                '}';
    }


    public static class HealthDocRowMapper implements RowMapper<HealthDoc> {
        @Override
        public HealthDoc mapRow(ResultSet rs, int rownum) throws SQLException {
            HealthDoc healthDoc = new HealthDoc();
            healthDoc.setId(rs.getLong("id"));
            healthDoc.setHealthId(rs.getString("healthId"));
            healthDoc.setDocName(rs.getString("docName"));
            healthDoc.setCustomerId(rs.getLong("customerId"));
            healthDoc.setDocOwner(rs.getString("docOwner"));
            healthDoc.setDocTypeId(rs.getInt("docTypeId"));
            healthDoc.setDocS3Url(rs.getString("docS3Url"));
            healthDoc.setDocTags(rs.getString("docTags"));
            healthDoc.setMedicEntityName(rs.getString("medicEntityName"));
            healthDoc.setDoctorName(rs.getString("doctorName"));
            healthDoc.setIsActive(rs.getString("isActive"));
            healthDoc.setCreatedAt(rs.getTimestamp("createdAt"));
            healthDoc.setCreatedBy(rs.getLong("createdBy"));
            healthDoc.setUpdatedAt(rs.getTimestamp("updatedAt"));
            healthDoc.setUpdatedBy(rs.getLong("updatedBy"));
            return healthDoc;
        }
    }
}
