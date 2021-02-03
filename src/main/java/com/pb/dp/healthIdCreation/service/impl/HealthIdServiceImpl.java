package com.pb.dp.healthIdCreation.service.impl;

import com.google.gson.Gson;
import com.pb.dp.enums.ResponseStatus;
import com.pb.dp.healthIdCreation.dao.HealthIdDao;
import com.pb.dp.healthIdCreation.model.CreateHealthIdByMobRequest;
import com.pb.dp.healthIdCreation.model.Customer;
import com.pb.dp.healthIdCreation.model.CustomerDetails;
import com.pb.dp.healthIdCreation.model.MobileOtpPojo;
import com.pb.dp.healthIdCreation.service.HealthIdService;
import com.pb.dp.model.FieldKey;
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
    AuthTokenUtil authTokenUtil;

    String healthIdBaseURL = "https://healthidsbx.ndhm.gov.in/api/v1";
    String generateOtpMobile = "/registration/mobile/generateOtp";
    String verifyMobile = "/registration/mobile/verifyOtp";
    String resendMobile = "/registration/mobile/resendOtp";
    String hipId = "DPHIP119";

    @Override
    public Map<String, Object> registerViaMobile(CustomerDetails customerDetail) throws Exception {
        Map<String, Object> response = new HashMap<>();
        // add customer details
        this.healthIdDao.addCustomer(customerDetail);
        //triggerOtp on mobile
        String txnId = this.generateOtp(customerDetail.getMobileNo());
        //save txnId
        this.healthIdDao.addNdhmOtpTxnId(customerDetail.getMobileNo(), txnId);

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
        String url = healthIdBaseURL + generateOtpMobile;
        Map<String, String> headers = new HashMap<>();
        headers.put("X-HIP-ID", hipId);
        headers.put("Authorization ", "Bearer " + this.authTokenUtil.bearerAuthToken());
        Map<String, Object> payload = new HashMap<>();
        payload.put("mobile", String.valueOf(mobile));
        response = HttpUtil.post(url, new Gson().toJson(payload), headers);
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
    public Map<String, Object> verifyViaMobile(MobileOtpPojo mobileOtpPojo, Integer custId) throws Exception{
        Map<String, Object> response = new HashMap<>();
        //add otp to DB
        this.healthIdDao.updateNdhmOTP(mobileOtpPojo);
        //verify OTP
        String token = this.verifyMobileOtp(mobileOtpPojo);
        mobileOtpPojo.setToken(token);
        //update ndhm_Otp token
        this.healthIdDao.updateNdhmOtpToken(mobileOtpPojo);
        //create healthId on ndhm
        this.createHeathId(custId,mobileOtpPojo.getMobile(),mobileOtpPojo.getTxnId(),token);
        if (ObjectUtils.isNotEmpty(token)) {
            response.put("mobileNo", mobileOtpPojo.getMobile());
            response.put(FieldKey.SK_STATUS_MESSAGE, ResponseStatus.SUCCESS.getStatusMsg());
            response.put(FieldKey.SK_STATUS_CODE, ResponseStatus.SUCCESS.getStatusId());
        } else {
            response.put(FieldKey.SK_STATUS_MESSAGE, ResponseStatus.FAILURE.getStatusMsg());
            response.put(FieldKey.SK_STATUS_CODE, ResponseStatus.FAILURE.getStatusId());
        }
        return response;
    }

    private String verifyMobileOtp(MobileOtpPojo mobileOtpPojo) throws Exception{
        Map<String, Object> response = new HashMap<>();
        String token = null;
        String url = healthIdBaseURL + verifyMobile;
        Map<String, String> headers = new HashMap<>();
        headers.put("X-HIP-ID", hipId);
        headers.put("Authorization ", this.authTokenUtil.bearerAuthToken());
        Map<String, Object> payload = new HashMap<>();
        payload.put("otp", String.valueOf(mobileOtpPojo.getOtp()));
        payload.put("txnId", String.valueOf(mobileOtpPojo.getOtp()));
        response = HttpUtil.post(url, new Gson().toJson(payload), headers);
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
        //get customer from Db
        Customer customer = this.healthIdDao.getCustomer(custId,mobile);
        //form payload
        CreateHealthIdByMobRequest createHealthIdRequest = new CreateHealthIdByMobRequest();
        createHealthIdRequest.setFirstName(customer.getFirstName());
        createHealthIdRequest.setLastName(customer.getLastName());
        createHealthIdRequest.setName(customer.getFirstName()+customer.getLastName());
        //createHealthIdRequest.setHealthId(customer.getHealthId());
        createHealthIdRequest.setTxnId(txnId);
        createHealthIdRequest.setToken(token);
        createHealthIdRequest.setEmail(customer.getEmailId());
        createHealthIdRequest.setGender(customer.getGender());
        Date dob = customer.getDob();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String dobString = sdf.format(dob);
        String[] dobArray = dobString.split("-");
        String date = dobArray[0];
        String month = dobArray[0];
        String year = dobArray[0];
        createHealthIdRequest.setDayOfBirth(date);
        createHealthIdRequest.setMonthOfBirth(month);
        createHealthIdRequest.setYearOfBirth(year);

        String url = healthIdBaseURL + verifyMobile;
        Map<String, String> headers = new HashMap<>();
        headers.put("X-HIP-ID", hipId);
        headers.put("Authorization ", this.authTokenUtil.bearerAuthToken());

        response = HttpUtil.post(url, new Gson().toJson(createHealthIdRequest), headers);
        if(Objects.nonNull(response)) {
            Object responseBody = response.get("responseBody");
            if(Objects.nonNull(responseBody) && response.get("status").equals(200)) {
                Map<String, Object> responseBodyMap = new Gson().fromJson(responseBody.toString(), Map.class);

            }
        }

    }
}
