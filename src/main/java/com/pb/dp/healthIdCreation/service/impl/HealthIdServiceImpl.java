package com.pb.dp.healthIdCreation.service.impl;

import com.google.gson.Gson;
import com.pb.dp.dao.HealthDao;
import com.pb.dp.enums.ResponseStatus;
import com.pb.dp.healthIdCreation.dao.HealthIdDao;
import com.pb.dp.healthIdCreation.model.*;
import com.pb.dp.healthIdCreation.service.HealthIdService;
import com.pb.dp.model.FieldKey;
import com.pb.dp.service.ConfigService;
import com.pb.dp.util.AuthTokenUtil;
import com.pb.dp.util.HttpUtil;

import org.apache.commons.lang3.ObjectUtils;
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
    public Map<String, Object> verifyForRegistration(NdhmMobOtpRequest ndhmMobOtpRequest, Integer custId) throws Exception{
        Map<String, Object> response = new HashMap<>();
        //verify OTP
        String token = this.verifyMobileOtp(ndhmMobOtpRequest);
        if (ObjectUtils.isNotEmpty(token)) {
            ndhmMobOtpRequest.setToken(token);
            //update ndhm mobile token
            this.healthIdDao.updateNdhmOtpToken(ndhmMobOtpRequest, custId);
            //create healthId on ndhm
            this.createHeathId(custId, ndhmMobOtpRequest.getMobile(), ndhmMobOtpRequest.getTxnId(), token);

            response.put("mobileNo", ndhmMobOtpRequest.getMobile());
            response.put("verify", true);
            response.put(FieldKey.SK_STATUS_MESSAGE, ResponseStatus.SUCCESS.getStatusMsg());
            response.put(FieldKey.SK_STATUS_CODE, ResponseStatus.SUCCESS.getStatusId());
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

    private CreateHealthIdByMobRequest prepareHealthIdPayload(Integer custId, Long mobile, String txnId, String token) throws Exception{
        //get customer from Db
        CustomerDetails customer = this.healthIdDao.getCustomerDetails(custId);
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
        String dobString = customer.getDob();
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
    public Map<String, Object> updateHealthIdProfile(NdhmMobOtpRequest ndhmMobOtpRequest, int customerId) throws Exception {
        Map<String, Object> response = new HashMap<>();
        String authToken = this.authTokenUtil.bearerAuthToken();
        CustomerDetails customerDetails = this.healthIdDao.getCustomerDetails(customerId);
        String xToken = this.getValidToken(ndhmMobOtpRequest, authToken);
        if(ObjectUtils.isNotEmpty(xToken)) {
            UpdateAccountRequest updateAccountRequest = this.prepareUpdateProfilePayload(customerDetails);
            CustomerDetails updateProfileMap = this.updateProfileOnNdhm(updateAccountRequest, xToken);
            response.put("data", updateProfileMap);
            response.put(FieldKey.SK_STATUS_MESSAGE, ResponseStatus.SUCCESS.getStatusMsg());
            response.put(FieldKey.SK_STATUS_CODE, ResponseStatus.SUCCESS.getStatusId());
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
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        customerDetails.setDob(formatter.format(customerDetails.getDob()));
        String dobString = customerDetails.getDob();
        String[] dobArray = dobString.split("-");
        String date = dobArray[0];
        String month = dobArray[1];
        if(month.length() == 2 && month.substring(0)=="0"){
            month = month.substring(1);
        }
        String year = dobArray[2];
        updateAccountRequest.setDayOfBirth(date);
        updateAccountRequest.setMonthOfBirth(month);
        updateAccountRequest.setYearOfBirth(year);
        return updateAccountRequest;
    }

    private String getValidToken(NdhmMobOtpRequest ndhmMobOtpRequest, String authToken) throws Exception {
        String token = null;
        boolean isValidToken = true;
        StringBuilder xToken = new StringBuilder("Bearer ");
        String oldToken = healthDao.getHealthToken(ndhmMobOtpRequest.getHealthId());
        if (null != oldToken) {
            isValidToken = authTokenUtil.isValidToken(oldToken, token);
            if (isValidToken) {
                xToken.append(oldToken);
            } else {
                xToken.append(this.confirmWithOtp(ndhmMobOtpRequest, authToken));
            }
        }
        token = xToken.toString();
        return token;
    }

    private String confirmWithOtp(NdhmMobOtpRequest ndhmMobOtpRequest, String authToken) throws Exception {
        Map<String, Object> response = new HashMap<>();
        String token = null;
        String url = configService.getPropertyConfig("NDHM_CONFIRM_MOB_OTP_URL").getValue();
        Map<String, String> headers = new HashMap<>();
        headers.put("X-HIP-ID", hipId);
        headers.put("Authorization", authToken);
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

    @Override
    public Map<String, Object> generateOtpForUpdate(CustomerDetails customerDetails) throws Exception {
        Map<String, Object> response = new HashMap<>();
        //Todo update customer data to DB
        String txnId = this.authTokenUtil.authInit(customerDetails.getHealthId());
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

    private CustomerDetails updateProfileOnNdhm(UpdateAccountRequest payload, String xToken) throws Exception {
        CustomerDetails customerDetails = null;
        Map<String, Object> responseBodyMap = new HashMap<>();
        String url = configService.getPropertyConfig("NDHM_ACCOUNT_PROFILE_URL").getValue();
        Map<String, String> headers = new HashMap<>();
        headers.put("X-HIP-ID", hipId);
        headers.put("Authorization", this.authTokenUtil.bearerAuthToken());
        headers.put("X-Token", xToken);
        Map<String, Object> response = HttpUtil.post(url, new Gson().toJson(payload), headers);
        if(Objects.nonNull(response)) {
            Object responseBody = response.get("responseBody");
            if(Objects.nonNull(responseBody) && response.get("status").equals(200)) {
                responseBodyMap = new Gson().fromJson(responseBody.toString(), Map.class);
                if (null != responseBodyMap) {
                    customerDetails = new CustomerDetails();
                    customerDetails.setMobileNo(Long.valueOf((String) responseBodyMap.get("mobile")));
                    customerDetails.setFullName((String) responseBodyMap.get("name"));
                    customerDetails.setEmailId((String) responseBodyMap.get("email"));
                    customerDetails.setAddress((String) responseBodyMap.get("address"));
                    customerDetails.setState(Long.valueOf((String) responseBodyMap.get("stateCode")));
                    customerDetails.setStateName((String) responseBodyMap.get("stateName"));
                    customerDetails.setDistrict(Long.valueOf((String) responseBodyMap.get("districtCode")));
                    customerDetails.setDistrictName((String) responseBodyMap.get("districtName"));
                    customerDetails.setHealthId((String) responseBodyMap.get("healthId"));
                    customerDetails.setHealthIdNo((String) responseBodyMap.get("healthIdNumber"));
                    customerDetails.setGender((String) responseBodyMap.get("gender"));
                    customerDetails.setFirstName((String) responseBodyMap.get("firstName"));
                    //customerDetails.setMidName((String) responseBodyMap.get("middleName"));
                    customerDetails.setLastName((String) responseBodyMap.get("lastName"));
                    customerDetails.setKyc((Boolean)responseBodyMap.get("kycVerified"));
                }
            }
        }
        return customerDetails;
    }
}
