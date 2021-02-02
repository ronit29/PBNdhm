package com.pb.dp.model;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.util.StringUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class OAuthUrlInfo {

	// {
	// "url": "product/list",
	// "H_M":"get",
	// "RQ_P":null,
	// "dmns":[],
	// "OAuth":true,
	// "M_F":[],
	// "EXPRS":15,
	// "C_AT":"",
	// ROLE:[]
	// }

	public static final String URL = "url";
	public static final String HTTP_METHOD_TYPE = "H_M";
	public static final String REQUIRED_PARAMETERS = "RQ_P";
	public static final String ALLOWED_DOMAINS = "dmns";
	public static final String OAUTH = "OAuth";
	public static final String MANDATORY_FIELDS = "M_F";
	public static final String EXPITY_DURATION = "EXPRS";
	public static final String CREATED_AT = "C_AT";
	public static final String ROLE = "ROLE";
	public static final String SECURED_URL_ROLE = "SECURED";
	public static final String IS_MYACC_SERVICE = "MYACC_SERVICE";

	private String url;
	private String httpMethodType;
	private List<String> requiredParameters;
	private List<String> mandatoryFields;
	private List<String> allowedDomains;
	private List<String> roles;
	private boolean isOauthRequired;
	private int expiryDuration;
	private Date createdAt;
	private boolean isMyAccountService;


	public boolean isMyAccountService() {
		return isMyAccountService;
	}

	public void setMyAccountService(boolean isMyAccountService) {
		this.isMyAccountService = isMyAccountService;
	}

	public boolean isSecuredUrl() {
		return this.roles.contains(SECURED_URL_ROLE);
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getHttpMethodType() {
		return httpMethodType;
	}

	public void setHttpMethodType(String httpMethodType) {
		this.httpMethodType = httpMethodType;
	}

	public List<String> getRequiredParameters() {
		return requiredParameters;
	}

	public void setRequiredParameters(List<String> requiredParameters) {
		this.requiredParameters = requiredParameters;
	}

	public void setRequiredParameters(String requiredParameters) {
		if (!StringUtils.isEmpty(requiredParameters))
			this.requiredParameters = Arrays.asList(StringUtils.tokenizeToStringArray(requiredParameters, ","));
	}

	public List<String> getMandatoryFields() {
		return mandatoryFields;
	}

	public void setMandatoryFields(List<String> mandatoryFields) {
		this.mandatoryFields = mandatoryFields;
	}

	public void setMandatoryFields(String mandatoryFields) {
		if (!StringUtils.isEmpty(mandatoryFields))
			this.mandatoryFields = Arrays.asList(StringUtils.tokenizeToStringArray(mandatoryFields, ","));
	}

	public List<String> getAllowedDomains() {
		return allowedDomains;
	}

	public void setAllowedDomains(List<String> allowedDomains) {
		this.allowedDomains = allowedDomains;
	}

	public void setAllowedDomains(String allowedDomains) {
		if (!StringUtils.isEmpty(allowedDomains))
			this.allowedDomains = Arrays.asList(StringUtils.tokenizeToStringArray(allowedDomains, ","));
	}

	public List<String> getRoles() {
		return roles;
	}

	public void setRoles(List<String> roles) {
		this.roles = roles;
	}

	public void setRoles(String roles) {
		if (!StringUtils.isEmpty(roles))
			this.roles = Arrays.asList(StringUtils.tokenizeToStringArray(roles, ","));
	}

	public boolean isOauthRequired() {
		return isOauthRequired;
	}

	public void setOauthRequired(boolean isOauthRequired) {
		this.isOauthRequired = isOauthRequired;
	}

	public int getExpiryDuration() {
		return expiryDuration;
	}

	public void setExpiryDuration(int expiryDuration) {
		this.expiryDuration = expiryDuration;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}


	public static class OAuthUrlInfoMapper implements RowMapper<OAuthUrlInfo> {
		@Override
		public OAuthUrlInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
			OAuthUrlInfo oAuthUrlInfo = new OAuthUrlInfo();
			oAuthUrlInfo.setUrl(rs.getString("url"));
			oAuthUrlInfo.setHttpMethodType(rs.getString("httpMethodType"));
			oAuthUrlInfo.setRequiredParameters(rs.getString("requiredParameters"));
			oAuthUrlInfo.setAllowedDomains(rs.getString("allowedDomains"));
			oAuthUrlInfo.setRoles(rs.getString("roles"));
			oAuthUrlInfo.setOauthRequired(rs.getBoolean("isOauthRequired"));
			oAuthUrlInfo.setExpiryDuration(rs.getInt("expiryDuration"));
			oAuthUrlInfo.setMandatoryFields(rs.getString("mandatoryFileds"));
			oAuthUrlInfo.setCreatedAt(rs.getDate("createdOn"));
			oAuthUrlInfo.setMyAccountService(rs.getBoolean("isMyAccountService"));
			return oAuthUrlInfo;
		}
	}
}
