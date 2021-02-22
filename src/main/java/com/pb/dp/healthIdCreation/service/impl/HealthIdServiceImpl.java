package com.pb.dp.healthIdCreation.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.pb.dp.dao.HealthDao;
import com.pb.dp.enums.ResponseStatus;
import com.pb.dp.healthIdCreation.dao.HealthIdDao;
import com.pb.dp.healthIdCreation.enums.Relationship;
import com.pb.dp.healthIdCreation.model.*;
import com.pb.dp.healthIdCreation.service.HealthIdService;
import com.pb.dp.model.FieldKey;
import com.pb.dp.service.ConfigService;
import com.pb.dp.util.AuthTokenUtil;
import com.pb.dp.util.HttpUtil;

import com.pb.dp.util.LoggerUtil;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class HealthIdServiceImpl implements HealthIdService {

    @Autowired
    HealthIdDao healthIdDao;

    @Autowired
    HealthDao healthDao;

    @Autowired
    AuthTokenUtil authTokenUtil;

    @Autowired
    ConfigService configService;

    @Autowired
    LoggerUtil loggerUtil;

    String hipId = "DPHIP119";

    private static final Logger logger = LoggerFactory.getLogger(HealthIdServiceImpl.class);

    @Override
    public Map<String, Object> registerViaMobile(CustomerDetails customerDetail, int customerId) throws Exception {
        Map<String, Object> response = new HashMap<>();
        HealthId healthId = null;
        Integer relation = 0;
        if (ObjectUtils.isEmpty(customerDetail.getRelationship())) {
            response.put(FieldKey.SK_STATUS_MESSAGE, "Relationship can not be empty or null");
            response.put(FieldKey.SK_STATUS_CODE, ResponseStatus.INVALID_INPUT.getStatusId());
        } else {
            relation = Relationship.valueOf(customerDetail.getRelationship().toUpperCase()).getRelationId();
            if (customerDetail.getRelationship().equalsIgnoreCase(Relationship.SELF.getRelation())) {
                //check if healthId for SELF exist
                healthId = this.healthIdDao.getHealthIdDetails(customerId, relation);
                if (ObjectUtils.isNotEmpty(healthId)) {
                    response.put(FieldKey.SK_STATUS_MESSAGE, "healthId for " + customerDetail.getRelationship() + " exists : " + healthId.getHealthId());
                    response.put(FieldKey.SK_STATUS_CODE, ResponseStatus.INVALID_INPUT.getStatusId());
                }
            }
            if (ObjectUtils.isEmpty(healthId)) {
                //get by healthId
                HealthId healthId1 = this.healthIdDao.getByHealth(customerDetail.getHealthId());
                if (null == healthId1) {
                    //triggerOtp on mobile
                    String txnId = this.generateOtp(customerDetail.getMobileNo());
                    if (ObjectUtils.isNotEmpty(txnId)) {
                        //TODO update and relationId & txnId in http session : not required
                        response.put("txnId", txnId);
                        response.put(FieldKey.SK_STATUS_MESSAGE, ResponseStatus.SUCCESS.getStatusMsg());
                        response.put(FieldKey.SK_STATUS_CODE, ResponseStatus.SUCCESS.getStatusId());

                    } else {
                        response.put(FieldKey.SK_STATUS_MESSAGE, ResponseStatus.FAILURE.getStatusMsg());
                        response.put(FieldKey.SK_STATUS_CODE, ResponseStatus.FAILURE.getStatusId());
                    }
                } else {
                    response.put(FieldKey.SK_STATUS_MESSAGE, ResponseStatus.INVALID_INPUT.getStatusMsg() + ": HealthId " + customerDetail.getHealthId() + " exist!!");
                    response.put(FieldKey.SK_STATUS_CODE, ResponseStatus.INVALID_INPUT.getStatusId());
                }
            }
        }
        return response;
    }

    private String generateOtp(Long mobile) throws Exception {
        Map<String, Object> response = new HashMap<>();
        String txnId = null;
        String url = configService.getPropertyConfig("NDHM_GEN_OTP_URL").getValue();
        Map<String, String> headers = new HashMap<>();
        headers.put("X-HIP-ID", hipId);
        headers.put("Authorization", this.authTokenUtil.bearerAuthToken());
        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("mobile", mobile.toString());
        String jsonPayload = new Gson().toJson(jsonMap);
        response = HttpUtil.post(url, jsonPayload, headers);
        if(Objects.nonNull(response)) {
            Object responseBody = response.get("responseBody");
            if(Objects.nonNull(responseBody) && response.get("status").equals(200)) {
                Map<String, Object> responseBodyMap = new Gson().fromJson(responseBody.toString(), Map.class);
                if(ObjectUtils.isNotEmpty(responseBodyMap.get("txnId"))) {
                    txnId = (String) responseBodyMap.get("txnId");
                }
            }
        }
        return txnId;
    }

    @Override
    public Map<String, Object> verifyForRegistration(NdhmMobOtpRequest ndhmMobOtpRequest, CustomerDetails customerProfileData, Integer custId) throws Exception {
        Map<String, Object> response = new HashMap<>();
        //verify OTP on ndhm
        String token = this.verifyMobileOtp(ndhmMobOtpRequest);
        if (ObjectUtils.isNotEmpty(token)) {
            ndhmMobOtpRequest.setToken(token);
            //create healthId on ndhm
            response = this.createHeathId(custId, customerProfileData, ndhmMobOtpRequest.getMobile(), ndhmMobOtpRequest.getTxnId(), token);
            if (response.get("statusCode").equals(200)) {
                CustomerDetails customerDetails = (CustomerDetails) response.get("data");
                if (ObjectUtils.isNotEmpty(customerDetails)) {
                    customerDetails.setTxnId(ndhmMobOtpRequest.getTxnId());
                    //Todo new = true/false check and its handling, also check or update the existing data if new false
                    Integer healthIdPk = this.healthIdDao.addHealthIdDemographics(customerDetails, custId);
                    if (healthIdPk > 0) {
                        response.put("data", customerDetails);
                        response.put("mobileNo", ndhmMobOtpRequest.getMobile());
                        response.put("verify", true);
                        response.put(FieldKey.SK_STATUS_MESSAGE, ResponseStatus.SUCCESS.getStatusMsg());
                        response.put(FieldKey.SK_STATUS_CODE, ResponseStatus.SUCCESS.getStatusId());
                    } else {
                        response.put("verify", false);
                        response.put(FieldKey.SK_STATUS_MESSAGE, ResponseStatus.FAILURE.getStatusMsg() + " : HealthId for user exist!!");
                        response.put(FieldKey.SK_STATUS_CODE, ResponseStatus.FAILURE.getStatusId());
                    }
                } else {
                    response.put("verify", false);
                    response.put(FieldKey.SK_STATUS_MESSAGE, ResponseStatus.NDHM_FAILURE.getStatusMsg() + " : HealthId Creation on NDHM failed");
                    response.put(FieldKey.SK_STATUS_CODE, ResponseStatus.NDHM_FAILURE.getStatusId());
                }
            }
        } else {
            response.put("verify", false);
            response.put(FieldKey.SK_STATUS_MESSAGE, ResponseStatus.FAILURE.getStatusMsg());
            response.put(FieldKey.SK_STATUS_CODE, ResponseStatus.FAILURE.getStatusId());
        }
        return response;
    }

    private String verifyMobileOtp(NdhmMobOtpRequest ndhmMobOtpRequest) throws Exception{
        Map<String, Object> response = new HashMap<>();
        String token = null;
        String url = configService.getPropertyConfig("NDHM_VERIFY_OTP_URL").getValue();
        Map<String, String> headers = new HashMap<>();
        headers.put("X-HIP-ID", hipId);
        headers.put("Authorization", this.authTokenUtil.bearerAuthToken());
        Map<String, Object> payload = new HashMap<>();
        payload.put("otp", String.valueOf(ndhmMobOtpRequest.getOtp()));
        payload.put("txnId", ndhmMobOtpRequest.getTxnId());
        String jsonPayload = new Gson().toJson(payload);
        response = HttpUtil.post(url, jsonPayload, headers);
        loggerUtil.logApiData(url,jsonPayload,headers,response);
        if(Objects.nonNull(response)) {
            Object responseBody = response.get("responseBody");
            if(Objects.nonNull(responseBody) && response.get("status").equals(200)) {
                Map<String, Object> responseBodyMap = new Gson().fromJson(responseBody.toString(), Map.class);
                if(ObjectUtils.isNotEmpty(responseBodyMap.get("token"))) {
                    token = (String) responseBodyMap.get("token");
                }
            }
        }
        return token;
    }

    private Map<String,Object> createHeathId(Integer custId, CustomerDetails customerProfileData, Long mobile, String txnId, String token) throws Exception {
        Map<String, Object> response = new HashMap<>();
        response.put(FieldKey.SK_STATUS_CODE, ResponseStatus.SUCCESS.getStatusId());
        CustomerDetails customerDetails = null;
        CreateHealthIdByMobRequest createHealthIdRequest = this.prepareHealthIdPayload(customerProfileData,mobile,txnId,token);
        String url = configService.getPropertyConfig("NDHM_CREATE_HEALTHID_URL").getValue();
        Map<String, String> headers = new HashMap<>();
        headers.put("X-HIP-ID", hipId);
        headers.put("Authorization", this.authTokenUtil.bearerAuthToken());
        String jsonPayload = new Gson().toJson(createHealthIdRequest);
        Map<String,Object> apiResponse = HttpUtil.post(url, jsonPayload, headers);
        loggerUtil.logApiData(url,jsonPayload,headers,apiResponse);
        if(Objects.nonNull(apiResponse)) {
            Object responseBody = apiResponse.get("responseBody");
            if(Objects.nonNull(responseBody)) {
                Map<String, Object> responseBodyMap = new Gson().fromJson(responseBody.toString(), Map.class);
                if (apiResponse.get("status").equals(200)) {
                    if (ObjectUtils.isNotEmpty(responseBodyMap)) {
                        customerDetails = new CustomerDetails();
                        customerDetails = this.prepareCustomerDetailsResponse(responseBodyMap);
                        customerDetails.setDob(customerProfileData.getDob());
                        customerDetails.setRelationship(customerProfileData.getRelationship());
                        customerDetails.setRelationId(Relationship.valueOf(customerProfileData.getRelationship().toUpperCase()).getRelationId());
                    }
                } else if (apiResponse.get("status").equals(422)) {
                    ObjectMapper mapper = new ObjectMapper();
                    List<Map<String, Object>> errorDetails = mapper.convertValue(responseBodyMap.get("details"), List.class);
                    String error = (String) errorDetails.get(0).get("message");
                    response.put(FieldKey.SK_STATUS_MESSAGE, ResponseStatus.NDHM_FAILURE.getStatusMsg()+ " :"+error);
                    response.put(FieldKey.SK_STATUS_CODE, ResponseStatus.NDHM_FAILURE.getStatusId());
                }
            }
        }
        response.put("data",customerDetails);
        return response;
    }

    private CreateHealthIdByMobRequest prepareHealthIdPayload(CustomerDetails customer, Long mobile, String txnId, String token) throws Exception{
        //form payload
        CreateHealthIdByMobRequest createHealthIdRequest = new CreateHealthIdByMobRequest();
        createHealthIdRequest.setFirstName(customer.getFirstName());
        createHealthIdRequest.setLastName(customer.getLastName());
        createHealthIdRequest.setName(customer.getFirstName()+ " " +customer.getLastName());
        if(customer.getHealthId().contains("@")){
            String healthId = customer.getHealthId();
            String[] healthIdVal = healthId.split("@");
            customer.setHealthId(healthIdVal[0]);
        }
        createHealthIdRequest.setHealthId(customer.getHealthId());
        createHealthIdRequest.setTxnId(txnId);
        createHealthIdRequest.setToken(token);
        createHealthIdRequest.setEmail(customer.getEmailId());
        createHealthIdRequest.setGender(customer.getGender());
        String dobString = customer.getDob();
        String[] dobArray = dobString.split("-");
        String date = dobArray[0];
        String month = dobArray[1];
        if(month.length() == 2 && month.substring(0,1).equalsIgnoreCase("0")){
            month = month.substring(1);
        }
        String year = dobArray[2];
        createHealthIdRequest.setDayOfBirth(date);
        createHealthIdRequest.setMonthOfBirth(month);
        createHealthIdRequest.setYearOfBirth(year);
        createHealthIdRequest.setAddress(customer.getAddress());
        createHealthIdRequest.setDistrictCode(customer.getDistrict().toString());
        createHealthIdRequest.setStateCode(customer.getState().toString());
        return createHealthIdRequest;
    }

    @Override
    public Map<String, Object> resendNdhmOtp(String txnId) throws Exception {
        Map<String, Object> response = new HashMap<>();
        Boolean resend = false;
        String url = configService.getPropertyConfig("NDHM_RESEND_OTP_URL").getValue();
        Map<String, String> headers = new HashMap<>();
        headers.put("X-HIP-ID", hipId);
        headers.put("Authorization", this.authTokenUtil.bearerAuthToken());
        Map<String, Object> payload = new HashMap<>();
        payload.put("txnId",txnId);
        String jsonPayload = new Gson().toJson(payload);
        Map<String, Object> responseMap = HttpUtil.post(url, new Gson().toJson(payload), headers);
        loggerUtil.logApiData(url,jsonPayload,headers,response);
        if(Objects.nonNull(responseMap)) {
            if(Objects.nonNull(responseMap.get("responseBody")) && responseMap.get("status").equals(200)) {
                String responseBody = (String) responseMap.get("responseBody");
                resend  = Boolean.valueOf(responseBody);
            }
        }
        if (resend) {
            response.put("resend",resend);
            response.put(FieldKey.SK_STATUS_MESSAGE, ResponseStatus.SUCCESS.getStatusMsg());
            response.put(FieldKey.SK_STATUS_CODE, ResponseStatus.SUCCESS.getStatusId());
        } else {
            response.put(FieldKey.SK_STATUS_MESSAGE, ResponseStatus.FAILURE.getStatusMsg());
            response.put(FieldKey.SK_STATUS_CODE, ResponseStatus.FAILURE.getStatusId());
        }
        return response;
    }

    @Override
    public Map<String, Object> updateHealthIdProfile(NdhmMobOtpRequest ndhmMobOtpRequest, CustomerDetails customerDetails, int customerId) throws Exception {
        Map<String, Object> response = new HashMap<>();
        String authToken = authTokenUtil.bearerAuthToken();
 //       CustomerDetails customerDetails = this.healthIdDao.getCustomerDetails(customerId, ndhmMobOtpRequest.getMobile(), ndhmMobOtpRequest.getTxnId());
        String xToken = authTokenUtil.getValidToken(ndhmMobOtpRequest, authToken);
        if(ObjectUtils.isNotEmpty(xToken)) {
            UpdateAccountRequest updateAccountRequest = this.prepareUpdateProfilePayload(customerDetails);
            CustomerDetails updatedProfileMap = this.updateProfileOnNdhm(updateAccountRequest, authToken, xToken);
            if(Objects.nonNull(updatedProfileMap)) {
                updatedProfileMap.setDob(customerDetails.getDob());
                if(!ObjectUtils.isEmpty(customerDetails.getRelationship())) {
                    updatedProfileMap.setRelationId(Relationship.valueOf(customerDetails.getRelationship().toUpperCase()).getRelationId());
                    updatedProfileMap.setRelationship(customerDetails.getRelationship());
                }
                this.healthIdDao.updateProfileData(updatedProfileMap,customerId);
                response.put("data", updatedProfileMap);
                response.put(FieldKey.SK_STATUS_MESSAGE, ResponseStatus.SUCCESS.getStatusMsg());
                response.put(FieldKey.SK_STATUS_CODE, ResponseStatus.SUCCESS.getStatusId());
            } else{
                response.put(FieldKey.SK_STATUS_MESSAGE, ResponseStatus.FAILURE.getStatusMsg());
                response.put(FieldKey.SK_STATUS_CODE, ResponseStatus.FAILURE.getStatusId());
            }
        }
        else{
            response.put(FieldKey.SK_STATUS_MESSAGE, ResponseStatus.FAILURE.getStatusMsg());
            response.put(FieldKey.SK_STATUS_CODE, ResponseStatus.FAILURE.getStatusId());
        }
        return response;
    }

    private UpdateAccountRequest prepareUpdateProfilePayload(CustomerDetails customerDetails) {
        UpdateAccountRequest updateAccountRequest = new UpdateAccountRequest();
        updateAccountRequest.setFirstName(customerDetails.getFirstName());
        updateAccountRequest.setLastName(customerDetails.getLastName());
        updateAccountRequest.setEmail(customerDetails.getEmailId());
        updateAccountRequest.setStateCode(customerDetails.getState().toString());
        updateAccountRequest.setDistrictCode(customerDetails.getDistrict().toString());
        updateAccountRequest.setAddress(customerDetails.getAddress());
        updateAccountRequest.setPincode(customerDetails.getPincode());
        String dobString = customerDetails.getDob();
        String[] dobArray = dobString.split("-");
        String date = dobArray[0];
        String month = dobArray[1];
        if(month.length() == 2 && month.substring(0,1).equalsIgnoreCase("0")){
            month = month.substring(1);
        }
        String year = dobArray[2];
        updateAccountRequest.setDayOfBirth(date);
        updateAccountRequest.setMonthOfBirth(month);
        updateAccountRequest.setYearOfBirth(year);
        updateAccountRequest.setGender(customerDetails.getGender());
        updateAccountRequest.setHealthId(customerDetails.getHealthId());
        updateAccountRequest.setProfilePhoto(customerDetails.getProfilePhoto());
        return updateAccountRequest;
    }

    @Override
    public Map<String, Object> generateOtpForUpdate(CustomerDetails customerDetails, int customerId) throws Exception {
        Map<String, Object> response = new HashMap<>();
//        if(!ObjectUtils.isEmpty(customerDetails.getRelationship()))
//            customerDetails.setRelationId(Relationship.valueOf(customerDetails.getRelationship().toUpperCase()).getRelationId());
        //this.healthIdDao.updateProfileData(customerDetails,customerId);
        String txnId = this.authTokenUtil.authInit(customerDetails.getHealthId());
        //this.healthIdDao.updateProfileTxnId(customerId,customerDetails.getHealthId(),txnId);
        if(ObjectUtils.isNotEmpty(txnId)){
            response.put("txnId",txnId);
            response.put(FieldKey.SK_STATUS_MESSAGE, ResponseStatus.SUCCESS.getStatusMsg());
            response.put(FieldKey.SK_STATUS_CODE, ResponseStatus.SUCCESS.getStatusId());
        } else {
            response.put(FieldKey.SK_STATUS_MESSAGE, ResponseStatus.FAILURE.getStatusMsg());
            response.put(FieldKey.SK_STATUS_CODE, ResponseStatus.FAILURE.getStatusId());
        }
        return response;
    }

    private CustomerDetails updateProfileOnNdhm(UpdateAccountRequest payload, String authToken, String xToken) throws Exception {
        CustomerDetails customerDetails = null;
        Map<String, Object> responseBodyMap = new HashMap<>();
        String url = configService.getPropertyConfig("NDHM_ACCOUNT_PROFILE_URL").getValue();
        Map<String, String> headers = new HashMap<>();
        headers.put("X-HIP-ID", hipId);
        headers.put("Authorization", authToken);
        headers.put("X-Token", xToken);
        String jsonPayload = new Gson().toJson(payload);
        Map<String, Object> response = HttpUtil.post(url, new Gson().toJson(payload), headers);
        loggerUtil.logApiData(url,jsonPayload,headers,response);
        if(Objects.nonNull(response)) {
            Object responseBody = response.get("responseBody");
            if(Objects.nonNull(responseBody) && response.get("status").equals(200)) {
                responseBodyMap = new Gson().fromJson(responseBody.toString(), Map.class);
                if(ObjectUtils.isNotEmpty(responseBodyMap)) {
                    customerDetails = this.prepareCustomerDetailsResponse(responseBodyMap);
                }
            }
        }
        return customerDetails;
    }

    private CustomerDetails prepareCustomerDetailsResponse(Map<String,Object> responseMap){
        CustomerDetails customerDetails = new CustomerDetails();
        if(ObjectUtils.isNotEmpty(responseMap.get("token"))){
            customerDetails.setToken((String) responseMap.get("token"));
        }
        customerDetails.setMobileNo(Long.valueOf((String) responseMap.get("mobile")));
        customerDetails.setFullName((String) responseMap.get("name"));
        customerDetails.setEmailId((String) responseMap.get("email"));
        customerDetails.setAddress((String) responseMap.get("address"));
        customerDetails.setState(Long.valueOf((String) responseMap.get("stateCode")));
        customerDetails.setStateName((String) responseMap.get("stateName"));
        customerDetails.setDistrict(Long.valueOf((String) responseMap.get("districtCode")));
        customerDetails.setDistrictName((String) responseMap.get("districtName"));
        customerDetails.setHealthId((String) responseMap.get("healthId"));
        customerDetails.setHealthIdNo((String) responseMap.get("healthIdNumber"));
        customerDetails.setGender((String) responseMap.get("gender"));
        customerDetails.setFirstName((String) responseMap.get("firstName"));
        //customerDetails.setMidName((String) responseMap.get("middleName"));
        customerDetails.setLastName((String) responseMap.get("lastName"));
        if(ObjectUtils.isNotEmpty(responseMap.get("kycVerified"))){
            customerDetails.setKyc((Boolean)responseMap.get("kycVerified"));
        }

        customerDetails.setProfilePhoto((String) responseMap.get("profilePhoto"));

        return customerDetails;
    }

	@Override
	public Map<String, Object> deleteHealthId(String healthId) throws Exception {
		 Map<String, Object> response = new HashMap<>();
		if(null!=healthId) {
			StringBuilder xToken = new StringBuilder("Bearer ");
			String authToken = healthDao.getHealthToken(healthId);
			String token = authTokenUtil.bearerAuthToken();
			if (null != authToken) {
				boolean isValidBoolean = authTokenUtil.isValidToken(authToken, token);
				if (isValidBoolean) {
					xToken.append(authToken);
					ndhmDeleteProfile(healthId, response, xToken.toString(), token);
				}else {
					response.put(FieldKey.SK_STATUS_MESSAGE, ResponseStatus.INVALID_XTOKEN.getStatusMsg());
		            response.put(FieldKey.SK_STATUS_CODE, ResponseStatus.INVALID_XTOKEN.getStatusId());
				}
			}else {
				response.put(FieldKey.SK_STATUS_MESSAGE, ResponseStatus.FAILURE.getStatusMsg());
				response.put(FieldKey.SK_STATUS_CODE, ResponseStatus.FAILURE.getStatusId());
			}
			
		}
		return response;
	}

	private void ndhmDeleteProfile(String healthId, Map<String, Object> response, String xToken, String token) {
		Map<String, String> header = new HashMap<>();
		header.put("Authorization", token);
		header.put("X-HIP-ID", hipId);
		header.put("X-Token", xToken);
		String url = configService.getPropertyConfig("NDHM_PROFILE_DELETE_URL").getValue();
		Map<String, Object> responseFromApi = HttpUtil.deleteWithHeader(url,header);
		loggerUtil.logApiData(url,null,header,responseFromApi);
		int statusCode = (int) responseFromApi.get("status");
		if (statusCode == 200) {
			String responseBody = (String) responseFromApi.get("responseBody");
			boolean isValid  = Boolean.valueOf(responseBody);
			if(isValid) {
				response.put(FieldKey.SK_STATUS_MESSAGE, ResponseStatus.SUCCESS.getStatusMsg());
				response.put(FieldKey.SK_STATUS_CODE, ResponseStatus.SUCCESS.getStatusId());
				healthDao.deleteHealthId(healthId);
			}else {
				response.put(FieldKey.SK_STATUS_MESSAGE, ResponseStatus.FAILURE.getStatusMsg());
				response.put(FieldKey.SK_STATUS_CODE, ResponseStatus.FAILURE.getStatusId());
			}
		}
	}

	@Override
	public Map<String, Object> deleteHealthId(NdhmMobOtpRequest ndhmMobOtpRequest) throws Exception {
		Map<String, Object> response = new HashMap<>();
		if(null!=ndhmMobOtpRequest.getHealthId()) {
			String authToken = authTokenUtil.bearerAuthToken();
			String xToken = authTokenUtil.getValidToken(ndhmMobOtpRequest, authToken);
			if (null != authToken) {
				ndhmDeleteProfile(ndhmMobOtpRequest.getHealthId(), response, xToken, authToken);
			}else {
				response.put(FieldKey.SK_STATUS_MESSAGE, ResponseStatus.FAILURE.getStatusMsg());
				response.put(FieldKey.SK_STATUS_CODE, ResponseStatus.FAILURE.getStatusId());
			}
		}
		return response;
	}

	@Override
	public Map<String, Object> registerWithAadhar(RegisterAadharRequest registerAadharRequest) throws Exception {
		Map<String, Object> response = new HashMap<>();
		if (null != registerAadharRequest.getHealthId()) {
			StringBuilder xToken = new StringBuilder("Bearer ");
			String authToken = healthDao.getHealthToken(registerAadharRequest.getHealthId());
			String token = authTokenUtil.bearerAuthToken();
			if (null != authToken) {
				boolean isValidBoolean = authTokenUtil.isValidToken(authToken, token);
				if (isValidBoolean) {
					xToken.append(authToken);
					registerWithAadhar(response, xToken.toString(), token);
				} else {
					response.put(FieldKey.SK_STATUS_MESSAGE, ResponseStatus.INVALID_XTOKEN.getStatusMsg());
					response.put(FieldKey.SK_STATUS_CODE, ResponseStatus.INVALID_XTOKEN.getStatusId());
				}
			} else {
				response.put(FieldKey.SK_STATUS_MESSAGE, ResponseStatus.FAILURE.getStatusMsg());
				response.put(FieldKey.SK_STATUS_CODE, ResponseStatus.FAILURE.getStatusId());
			}

		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private void registerWithAadhar(Map<String, Object> response, String xToken, String token) throws Exception {
		Map<String, String> header = new HashMap<>();
		header.put("Authorization", token);
		header.put("X-HIP-ID", hipId);
		header.put("X-Token", xToken);
		String url = configService.getPropertyConfig("NDHM_REGISTER_AADHAR_URL").getValue();
		Map<String, Object> jsonMap = new HashMap<>();
		String jsonPayload = new Gson().toJson(jsonMap);
		Map<String, Object> responseFromApi = HttpUtil.post(url,jsonPayload,header);
		loggerUtil.logApiData(url,jsonPayload,header,responseFromApi);
		int statusCode = (int) responseFromApi.get("status");
		if (statusCode == 200) {
			String responseBody = (String) responseFromApi.get("responseBody");
			Map<String, Object> responseMap = (Map<String, Object>) new Gson().fromJson(responseBody, Map.class);
			String txnId = (String) responseMap.get("txnId");
			response.put(FieldKey.SK_STATUS_MESSAGE, ResponseStatus.SUCCESS.getStatusMsg());
			response.put(FieldKey.SK_STATUS_CODE, ResponseStatus.SUCCESS.getStatusId());
			response.put("txnId", txnId);
		}else {
			response.put(FieldKey.SK_STATUS_MESSAGE, ResponseStatus.FAILURE.getStatusMsg());
			response.put(FieldKey.SK_STATUS_CODE, ResponseStatus.FAILURE.getStatusId());
		}
	}

	@Override
	public Map<String, Object> verifyOtpWithAadhar(VerifyOtpWithAadharRequest verifyOtpWithAadharRequest)
			throws Exception {
		Map<String, Object> response = new HashMap<>();
		StringBuilder xToken = new StringBuilder("Bearer ");
		String authToken = healthDao.getHealthToken(verifyOtpWithAadharRequest.getHealthId());
		String token = authTokenUtil.bearerAuthToken();
		if (null != authToken) {
			boolean isValidBoolean = authTokenUtil.isValidToken(authToken, token);
			if (isValidBoolean) {
				xToken.append(authToken);
				verifyOtpWithAadhar(response, xToken.toString(), token,verifyOtpWithAadharRequest);
			} else {
				response.put(FieldKey.SK_STATUS_MESSAGE, ResponseStatus.INVALID_XTOKEN.getStatusMsg());
				response.put(FieldKey.SK_STATUS_CODE, ResponseStatus.INVALID_XTOKEN.getStatusId());
			}
		} else {
			response.put(FieldKey.SK_STATUS_MESSAGE, ResponseStatus.FAILURE.getStatusMsg());
			response.put(FieldKey.SK_STATUS_CODE, ResponseStatus.FAILURE.getStatusId());
		}

		return response;
	}

	private void verifyOtpWithAadhar(Map<String, Object> response, String xToken, String token,
			VerifyOtpWithAadharRequest verifyOtpWithAadharRequest) throws Exception {
		Map<String, String> header = new HashMap<>();
		header.put("Authorization", token);
		header.put("X-HIP-ID", hipId);
		header.put("X-Token", xToken);
		String url = configService.getPropertyConfig("NDHM_VERIFY_AADHAR_URL").getValue();
		Map<String, Object> jsonMap = new HashMap<>();
		jsonMap.put("otp", verifyOtpWithAadharRequest.getOtp());
		jsonMap.put("restrictions", verifyOtpWithAadharRequest.getRestrictions());
		jsonMap.put("txnId", verifyOtpWithAadharRequest.getTxnId());
		String jsonPayload = new Gson().toJson(jsonMap);
		Map<String, Object> responseFromApi = HttpUtil.post(url,jsonPayload,header);
		loggerUtil.logApiData(url,jsonPayload,header,responseFromApi);
		int statusCode = (int) responseFromApi.get("status");
		if (statusCode == 200) {
			String responseBody = (String) responseFromApi.get("responseBody");
			boolean isValidBoolean  = Boolean.valueOf(responseBody);
			if(isValidBoolean) {
				response.put(FieldKey.SK_STATUS_MESSAGE, ResponseStatus.SUCCESS.getStatusMsg());
				response.put(FieldKey.SK_STATUS_CODE, ResponseStatus.SUCCESS.getStatusId());
			}else {
				response.put(FieldKey.SK_STATUS_MESSAGE, ResponseStatus.FAILURE.getStatusMsg());
				response.put(FieldKey.SK_STATUS_CODE, ResponseStatus.FAILURE.getStatusId());
			}
		}else {
			response.put(FieldKey.SK_STATUS_MESSAGE, ResponseStatus.FAILURE.getStatusMsg());
			response.put(FieldKey.SK_STATUS_CODE, ResponseStatus.FAILURE.getStatusId());
		}
		
	}
}
