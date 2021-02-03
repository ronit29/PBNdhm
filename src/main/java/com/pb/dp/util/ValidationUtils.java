package com.pb.dp.util;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pb.dp.filter.OAuthSecurityFilter;
import com.pb.dp.service.ConfigService;


@Component
public class ValidationUtils {

	private static final String mobileNumberPattern = "^(?:0091|\\+91|0|)[1-9][0-9]{9}$";
	private static final String customerIdPattern = "\\d+";
	private static final String applicationIdPattern = "^[a-zA-Z0-9]*$";
	private static final String emailIdPattern = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	private static final String datePattern_DDMMYYYY = "^[0-3][0-9]-[0-3][0-9]-(?:[0-9][0-9])?[0-9][0-9]$";// dd-MM-yyyy
	private static final String numericStringPattern = "\\d+";

	public static boolean isValidMobileNumber(String mobile) {
		return (mobile == null || mobile.isEmpty()) ? false : mobile.matches(mobileNumberPattern);
	}

	public static boolean isValidCustomerId(String customerId) {
		return (customerId == null || customerId.isEmpty()) ? false : customerId.matches(customerIdPattern);
	}
	
	public static boolean isValidApplicationId(String appId) {
		return (appId == null || appId.isEmpty()) ? false : appId.matches(applicationIdPattern);
	}

	public static boolean isValidEmailId(String emailId) {
		return (emailId == null || emailId.isEmpty()) ? false : emailId.matches(emailIdPattern);
	}

	public static boolean isValidOAuthKey(String key, String code) {
		if (code.equals(OAuthSecurityFilter.MOBILE_ID_CODE)) {
			return isValidMobileNumber(key);
		}
		if (code.equals(OAuthSecurityFilter.CUSTOMER_ID_CODE)) {
			return isValidCustomerId(key);
		}
		if (code.equals(OAuthSecurityFilter.APPLICATION_ID_CODE)) {
			return isValidApplicationId(key);
		}
		return false;
	}

	public static boolean isValidDate(String dateString) {
		return (dateString == null || dateString.isEmpty()) ? false : dateString.matches(datePattern_DDMMYYYY);
	}
	
	public static boolean isValidNumericString(String input) {
		return (input == null || input.isEmpty()) ? false : input.matches(numericStringPattern);
	}
	
	/*
	 * public Map<Boolean,CustomerSegmentResponse>
	 * validateCustomerSegmentRequest(CustomerSegmentResponse
	 * customerSegmentResponse, String authorizationHeader, String clientKey) {
	 * Map<Boolean,CustomerSegmentResponse> validationResponse = new
	 * HashMap<Boolean, CustomerSegmentResponse>(); if(null != clientKey) {
	 * Map<String, AuthDetail> configMap = configService.getAuthDetails();
	 * customerSegmentResponse.setStatusCode(ResponseStatus.INVALID_CLIENT_KEY.
	 * getStatusId());
	 * customerSegmentResponse.setStatusMsg(ResponseStatus.INVALID_CLIENT_KEY.
	 * getStatusMsg()); if(!configMap.containsKey(clientKey))
	 * validationResponse.put(true, customerSegmentResponse); }else {
	 * customerSegmentResponse.setStatusCode(ResponseStatus.CLIENT_KEY_NOT_PRESENT.
	 * getStatusId());
	 * customerSegmentResponse.setStatusMsg(ResponseStatus.CLIENT_KEY_NOT_PRESENT.
	 * getStatusMsg()); validationResponse.put(true, customerSegmentResponse); }
	 * return validationResponse; }
	 * 
	 * public Map<Boolean, ApiResponse> validateClientKey(String clientKey){
	 * Map<Boolean,ApiResponse > clientKeyValidationResp = new HashMap<Boolean,
	 * ApiResponse>(); ApiResponse apiResponse = new ApiResponse();
	 * apiResponse.setStatusCode(ResponseStatus.SUCCESS.getStatusId());
	 * apiResponse.setStatusMsg(ResponseStatus.SUCCESS.getStatusMsg());
	 * clientKeyValidationResp.put(false,apiResponse);
	 * if(Objects.nonNull(clientKey)) { Map<String, AuthDetail> configMap =
	 * configService.getAuthDetails(); if(!configMap.containsKey(clientKey)) {
	 * apiResponse.setStatusCode(ResponseStatus.INVALID_CLIENT_KEY.getStatusId());
	 * apiResponse.setStatusMsg(ResponseStatus.INVALID_CLIENT_KEY.getStatusMsg());
	 * clientKeyValidationResp.put(true,apiResponse); } }else {
	 * apiResponse.setStatusCode(ResponseStatus.CLIENT_KEY_NOT_PRESENT.getStatusId()
	 * );
	 * apiResponse.setStatusMsg(ResponseStatus.CLIENT_KEY_NOT_PRESENT.getStatusMsg()
	 * ); clientKeyValidationResp.put(true,apiResponse); } return
	 * clientKeyValidationResp;
	 * 
	 * }
	 */

	public static void main(String[] args) {
		String[] mobileNumbers = { "9972429140", "09972429140", "+919972429140", "99972429140", "2429140", "dkjhdjhkgd" };
		for (String m : mobileNumbers) {
			System.out.println("Mobile number[ " + m + " ] is valid[true/false] :: " + isValidMobileNumber(m));
		}

	}

}
