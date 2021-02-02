package com.pb.dp.model;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Objects;

public class PropertyConfigModel {
    private int id;
    private String key;
    private String value;
    private String remarks;
    private String type;
    private int appId;
    private Date createdOn;
    private int createdBy;
    private Date updatedOn;
    private int updatedBy;
    private boolean isActive;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getAppId() {
        return appId;
    }

    public void setAppId(int appId) {
        this.appId = appId;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }

    public Date getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(Date updatedOn) {
        this.updatedOn = updatedOn;
    }

    public int getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(int updatedBy) {
        this.updatedBy = updatedBy;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PropertyConfigModel that = (PropertyConfigModel) o;
        return id == that.id &&
                appId == that.appId &&
                createdBy == that.createdBy &&
                updatedBy == that.updatedBy &&
                isActive == that.isActive &&
                Objects.equals(key, that.key) &&
                Objects.equals(value, that.value) &&
                Objects.equals(remarks, that.remarks) &&
                Objects.equals(type, that.type) &&
                Objects.equals(createdOn, that.createdOn) &&
                Objects.equals(updatedOn, that.updatedOn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, key, value, remarks, type, appId, createdOn, createdBy, updatedOn, updatedBy, isActive);
    }

    @Override
    public String toString() {
        return "PropertyConfigModel{" +
                "id=" + id +
                ", key='" + key + '\'' +
                ", value='" + value + '\'' +
                ", remarks='" + remarks + '\'' +
                ", type='" + type + '\'' +
                ", appId=" + appId +
                ", createdOn=" + createdOn +
                ", createdBy=" + createdBy +
                ", updatedOn=" + updatedOn +
                ", updatedBy=" + updatedBy +
                ", isActive=" + isActive +
                '}';
    }

    public static class PropertyConfigModelMapper implements RowMapper<PropertyConfigModel> {
        @Override
        public PropertyConfigModel mapRow(ResultSet rs, int rowNum) throws SQLException {
            PropertyConfigModel propertyConfigModel = new PropertyConfigModel();
            propertyConfigModel.setId(rs.getInt("id"));
            propertyConfigModel.setKey(rs.getString("key"));
            propertyConfigModel.setValue(rs.getString("value"));
            propertyConfigModel.setRemarks(rs.getString("remarks"));
            propertyConfigModel.setType(rs.getString("type"));
            propertyConfigModel.setAppId(rs.getInt("appId"));
            propertyConfigModel.setCreatedOn(rs.getDate("createdOn"));
            propertyConfigModel.setCreatedBy(rs.getInt("createdBy"));
            propertyConfigModel.setUpdatedOn(rs.getDate("updatedOn"));
            propertyConfigModel.setUpdatedBy(rs.getInt("updatedBy"));
            propertyConfigModel.setActive(rs.getBoolean("isActive"));
            return propertyConfigModel;
        }
    }
}