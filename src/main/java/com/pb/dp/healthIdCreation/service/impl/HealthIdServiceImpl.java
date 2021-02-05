package com.pb.dp.healthIdCreation.service.impl;

import com.google.gson.Gson;
import com.pb.dp.dao.HealthDao;
import com.pb.dp.enums.ResponseStatus;
import com.pb.dp.healthIdCreation.dao.HealthIdDao;
import com.pb.dp.healthIdCreation.model.CreateHealthIdByMobRequest;
import com.pb.dp.healthIdCreation.model.Customer;
import com.pb.dp.healthIdCreation.model.CustomerDetails;
import com.pb.dp.healthIdCreation.model.NdhmMobOtpRequest;
import com.pb.dp.healthIdCreation.service.HealthIdService;
import com.pb.dp.model.FieldKey;
import com.pb.dp.service.ConfigService;
import com.pb.dp.util.AuthTokenUtil;
import com.pb.dp.util.HttpUtil;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.HashMap;
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

    String hipId = "DPHIP119";

    @Override
    public Map<String, Object> registerViaMobile(CustomerDetails customerDetail, int customerId) throws Exception {
        Map<String, Object> response = new HashMap<>();
        // add customer details
        Integer custId = this.healthIdDao.addCustomer(customerDetail,customerId);
        //triggerOtp on mobile
        String txnId = this.generateOtp(customerDetail.getMobileNo());
        //save txnId
        //this.healthIdDao.addNdhmOtpTxnId(customerDetail.getMobileNo(), txnId);
        this.healthIdDao.updateNdhmTxnId(custId,txnId);
        if (ObjectUtils.isNotEmpty(txnId)) {
            response.put("txnId", txnId);
            response.put(FieldKey.SK_STATUS_MESSAGE, ResponseStatus.SUCCESS.getStatusMsg());
            response.put(FieldKey.SK_STATUS_CODE, ResponseStatus.SUCCESS.getStatusId());
        } else {
            response.put(FieldKey.SK_STATUS_MESSAGE, ResponseStatus.FAILURE.getStatusMsg());
            response.put(FieldKey.SK_STATUS_CODE, ResponseStatus.FAILURE.getStatusId());
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
                txnId = (String)responseBodyMap.get("txnId");

            }
        }
        return txnId;
    }

    @Override
    public Map<String, Object> verifyViaMobile(NdhmMobOtpRequest ndhmMobOtpRequest, Integer custId) throws Exception{
        Map<String, Object> response = new HashMap<>();
        //add otp to DB
//        this.healthIdDao.updateNdhmOTP(ndhmMobOtpRequest);
        //verify OTP
        String token = this.verifyMobileOtp(ndhmMobOtpRequest);
        if (ObjectUtils.isNotEmpty(token)) {
        ndhmMobOtpRequest.setToken(token);
        //update ndhm_Otp token
        //this.healthIdDao.updateNdhmOtpToken(ndhmMobOtpRequest);
        //update ndhm mobile token
        this.healthIdDao.updateNdhmOtpToken(ndhmMobOtpRequest,custId);
        //create healthId on ndhm
        this.createHeathId(custId, ndhmMobOtpRequest.getMobile(), ndhmMobOtpRequest.getTxnId(),token);

            response.put("mobileNo", ndhmMobOtpRequest.getMobile());
            response.put("verify",true);
            response.put(FieldKey.SK_STATUS_MESSAGE, ResponseStatus.SUCCESS.getStatusMsg());
            response.put(FieldKey.SK_STATUS_CODE, ResponseStatus.SUCCESS.getStatusId());
        } else {
            response.put("verify",false);
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
        if(Objects.nonNull(response)) {
            Object responseBody = response.get("responseBody");
            if(Objects.nonNull(responseBody) && response.get("status").equals(200)) {
                Map<String, Object> responseBodyMap = new Gson().fromJson(responseBody.toString(), Map.class);
                token = (String)responseBodyMap.get("token");

            }
        }
        return token;
    }

    private void createHeathId(Integer custId, Long mobile, String txnId, String token) throws Exception {
        Map<String, Object> response = new HashMap<>();
        CreateHealthIdByMobRequest createHealthIdRequest = this.prepareHealthIdPayload(custId,mobile,txnId,token);

        String url = configService.getPropertyConfig("NDHM_CREATE_HEALTHID_URL").getValue();
        Map<String, String> headers = new HashMap<>();
        headers.put("X-HIP-ID", hipId);
        headers.put("Authorization", this.authTokenUtil.bearerAuthToken());

        response = HttpUtil.post(url, new Gson().toJson(createHealthIdRequest), headers);
        if(Objects.nonNull(response)) {
            Object responseBody = response.get("responseBody");
            if(Objects.nonNull(responseBody) && response.get("status").equals(200)) {
                Map<String, Object> responseBodyMap = new Gson().fromJson(responseBody.toString(), Map.class);

            }
        }

    }

    private CreateHealthIdByMobRequest prepareHealthIdPayload(Integer custId, Long mobile, String txnId, String token) {
        //get customer from Db
        Customer customer = this.healthIdDao.getCustomer(custId,mobile);
        //form payload
        CreateHealthIdByMobRequest createHealthIdRequest = new CreateHealthIdByMobRequest();
        createHealthIdRequest.setFirstName(customer.getFirstName());
        createHealthIdRequest.setLastName(customer.getLastName());
        createHealthIdRequest.setName(customer.getFirstName()+customer.getLastName());
        createHealthIdRequest.setHealthId(customer.getHealthId());
        createHealthIdRequest.setTxnId(txnId);
        createHealthIdRequest.setToken(token);
        createHealthIdRequest.setEmail(customer.getEmailId());
        createHealthIdRequest.setGender(customer.getGender());
        Date dob = customer.getDob();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String dobString = sdf.format(dob);
        String[] dobArray = dobString.split("-");
        String date = dobArray[0];
        String month = dobArray[1];
        if(month.length() == 2 && month.substring(0)=="0"){
            month = month.substring(1);
        }
        String year = dobArray[2];
        createHealthIdRequest.setDayOfBirth(date);
        createHealthIdRequest.setMonthOfBirth(month);
        createHealthIdRequest.setYearOfBirth(year);

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
        Map<String, Object> responseMap = HttpUtil.post(url, new Gson().toJson(payload), headers);
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
    public Map<String, Object> updateHealthIdProfile(CustomerDetails payload, int customerId) throws Exception{
        Map<String, Object> response = new HashMap<>();
        boolean isValidToken = true;
        if (ObjectUtils.isNotEmpty(payload.getHealthId())) {
            StringBuilder xToken = new StringBuilder("Bearer ");
            String authToken = healthDao.getHealthToken(payload.getHealthId());
            String token = authTokenUtil.bearerAuthToken();
            if (null != authToken) {
                isValidToken = authTokenUtil.isValidToken(authToken, token);
                if (isValidToken) {
                    xToken.append(authToken);
                    CustomerDetails customerDetails = this.updateProfileOnNdhm(payload,xToken.toString());
                    response.put("data", customerDetails);
                    response.put(FieldKey.SK_STATUS_MESSAGE, ResponseStatus.SUCCESS.getStatusMsg());
                    response.put(FieldKey.SK_STATUS_CODE, ResponseStatus.SUCCESS.getStatusId());
                } else {
                    response.put(FieldKey.SK_STATUS_MESSAGE, ResponseStatus.FAILURE.getStatusMsg());
                    response.put(FieldKey.SK_STATUS_CODE, ResponseStatus.FAILURE.getStatusId());
                }
            }
        }

        return response;
    }

    private CustomerDetails updateProfileOnNdhm(CustomerDetails customerDetails1, String xToken) throws Exception {
        CustomerDetails customerDetails = new CustomerDetails();
        String url = configService.getPropertyConfig("NDHM_ACCOUNT_PROFILE_URL").getValue();
        Map<String, String> headers = new HashMap<>();
        headers.put("X-HIP-ID", hipId);
        headers.put("Authorization", this.authTokenUtil.bearerAuthToken());
        headers.put("X-Token", xToken.toString());
        Map<String, Object> response = HttpUtil.post(url, new Gson().toJson(customerDetails), headers);
        if(Objects.nonNull(response)) {
            Object responseBody = response.get("responseBody");
            if(Objects.nonNull(responseBody) && response.get("status").equals(200)) {
                Map<String, Object> responseBodyMap = new Gson().fromJson(responseBody.toString(), Map.class);
                if (null != responseBodyMap) {
                    customerDetails.setEmailId((String) responseBodyMap.get("email"));
                    customerDetails.setAddress((String) responseBodyMap.get("address"));
                    customerDetails.setState((Integer)responseBodyMap.get("stateCode"));
                    customerDetails.setStateName((String) responseBodyMap.get("stateName"));
                    customerDetails.setDistrict((Integer)responseBodyMap.get("districtCode"));
                    customerDetails.setDistrictName((String) responseBodyMap.get("districtName"));
                    customerDetails.setHealthId((String) responseBodyMap.get("healthId"));
                    customerDetails.setHealthIdNo((String) responseBodyMap.get("healthIdNumber"));
                    customerDetails.setGender((String) responseBodyMap.get("gender"));
                    customerDetails.setFirstName((String) responseBodyMap.get("firstName"));
                    customerDetails.setMidName((String) responseBodyMap.get("middleName"));
                    customerDetails.setLastName((String) responseBodyMap.get("lastName"));
                    customerDetails.setKyc((Boolean)responseBodyMap.get("kycVerified"));
                }
            }
        }
        return customerDetails;
    }
    private String getTxnId(String token, String healthId) throws Exception {
        String txnId = null;
        Map<String, String> header = new HashMap<>();
        header.put("Authorization", token);
        header.put("X-HIP-ID", "DPHIP119");
        String url = configService.getPropertyConfig("NDHM_AUTH_INIT_URL").getValue();
        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("authMethod", "MOBILE_OTP");
        jsonMap.put("healthid", healthId);
        String jsonPayload = new Gson().toJson(jsonMap);
        Map<String, Object> responseFromApi = HttpUtil.post(url, jsonPayload, header);
        int statusCode2 = (int) responseFromApi.get("status");
        if (statusCode2 == 200) {
            String responseBody = (String) responseFromApi.get("responseBody");
            Map<String, Object> responseMap = (Map<String, Object>) new Gson().fromJson(responseBody, Map.class);
            txnId = (String) responseMap.get("txnId");
        }else {
            txnId = StringUtils.EMPTY;
        }
        return txnId;
    }
}
