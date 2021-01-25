package com.policybazaar.docprimNdhm.common.model;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Objects;

public class AuthDetail {
    private int id;
    private String auth_key;
    private int product_id;
    private String hash_client_key;
    private String hash_access_key;
    private String hash_secret_key;
    private String encryption_key;
    private String init_vector;
    private boolean is_enabled;
    private boolean is_hasing_enabled;
    private boolean is_encryption_enabled;
    private Date created_on;
    private Date updated_on;
    private String description;
    private String source;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAuth_key() {
        return auth_key;
    }

    public void setAuth_key(String auth_key) {
        this.auth_key = auth_key;
    }

    public int getProduct_id() {
        return product_id;
    }

    public void setProduct_id(int product_id) {
        this.product_id = product_id;
    }

    public String getHash_client_key() {
        return hash_client_key;
    }

    public void setHash_client_key(String hash_client_key) {
        this.hash_client_key = hash_client_key;
    }

    public String getHash_access_key() {
        return hash_access_key;
    }

    public void setHash_access_key(String hash_access_key) {
        this.hash_access_key = hash_access_key;
    }

    public String getHash_secret_key() {
        return hash_secret_key;
    }

    public void setHash_secret_key(String hash_secret_key) {
        this.hash_secret_key = hash_secret_key;
    }

    public String getEncryption_key() {
        return encryption_key;
    }

    public void setEncryption_key(String encryption_key) {
        this.encryption_key = encryption_key;
    }

    public String getInit_vector() {
        return init_vector;
    }

    public void setInit_vector(String init_vector) {
        this.init_vector = init_vector;
    }

    public boolean isIs_enabled() {
        return is_enabled;
    }

    public void setIs_enabled(boolean is_enabled) {
        this.is_enabled = is_enabled;
    }

    public boolean isIs_hasing_enabled() {
        return is_hasing_enabled;
    }

    public void setIs_hasing_enabled(boolean is_hasing_enabled) {
        this.is_hasing_enabled = is_hasing_enabled;
    }

    public boolean isIs_encryption_enabled() {
        return is_encryption_enabled;
    }

    public void setIs_encryption_enabled(boolean is_encryption_enabled) {
        this.is_encryption_enabled = is_encryption_enabled;
    }

    public Date getCreated_on() {
        return created_on;
    }

    public void setCreated_on(Date created_on) {
        this.created_on = created_on;
    }

    public Date getUpdated_on() {
        return updated_on;
    }

    public void setUpdated_on(Date updated_on) {
        this.updated_on = updated_on;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuthDetail that = (AuthDetail) o;
        return id == that.id &&
                product_id == that.product_id &&
                is_enabled == that.is_enabled &&
                is_hasing_enabled == that.is_hasing_enabled &&
                is_encryption_enabled == that.is_encryption_enabled &&
                Objects.equals(auth_key, that.auth_key) &&
                Objects.equals(hash_client_key, that.hash_client_key) &&
                Objects.equals(hash_access_key, that.hash_access_key) &&
                Objects.equals(hash_secret_key, that.hash_secret_key) &&
                Objects.equals(encryption_key, that.encryption_key) &&
                Objects.equals(init_vector, that.init_vector) &&
                Objects.equals(created_on, that.created_on) &&
                Objects.equals(updated_on, that.updated_on) &&
                Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, auth_key, product_id, hash_client_key, hash_access_key, hash_secret_key, encryption_key, init_vector, is_enabled, is_hasing_enabled, is_encryption_enabled, created_on, updated_on, description);
    }

    @Override
    public String toString() {
        return "AuthDetails{" +
                "id=" + id +
                ", auth_key='" + auth_key + '\'' +
                ", product_id=" + product_id +
                ", hash_client_key='" + hash_client_key + '\'' +
                ", hash_access_key='" + hash_access_key + '\'' +
                ", hash_secret_key='" + hash_secret_key + '\'' +
                ", encryption_key='" + encryption_key + '\'' +
                ", init_vector='" + init_vector + '\'' +
                ", is_enabled=" + is_enabled +
                ", is_hasing_enabled=" + is_hasing_enabled +
                ", is_encryption_enabled=" + is_encryption_enabled +
                ", created_on=" + created_on +
                ", updated_on=" + updated_on +
                ", description='" + description + '\'' +
                '}';
    }

    public static class AuthDetailMapper implements RowMapper<AuthDetail> {
        @Override
        public AuthDetail mapRow(ResultSet rs, int rowNum) throws SQLException {
            AuthDetail auth = new AuthDetail();
            auth.setId(rs.getInt("id"));
            auth.setAuth_key(rs.getString("auth_key"));
            auth.setProduct_id(rs.getInt("product_id"));
            auth.setHash_client_key(rs.getString("hash_client_key"));
            auth.setHash_access_key(rs.getString("hash_access_key"));
            auth.setHash_secret_key(rs.getString("hash_secret_key"));
            auth.setEncryption_key(rs.getString("encryption_key"));
            auth.setInit_vector(rs.getString("init_vector"));
            auth.setIs_enabled(rs.getBoolean("is_enabled"));
            auth.setIs_hasing_enabled(rs.getBoolean("is_hasing_enabled"));
            auth.setIs_encryption_enabled(rs.getBoolean("is_encryption_enabled"));
            auth.setCreated_on(rs.getDate("created_on"));
            auth.setUpdated_on(rs.getDate("updated_on"));
            auth.setDescription(rs.getString("description"));
            auth.setSource(rs.getString("source"));
            return auth;
        }
    }

}
