package com.pb.dp.service.impl;

import com.google.gson.Gson;
import com.pb.dp.model.PatientAuth;
import com.pb.dp.model.PatientAuthNotification;
import com.pb.dp.model.PatientAuthRequester;
import com.pb.dp.service.LockerAuthService;
import com.pb.dp.util.AuthTokenUtil;
import com.pb.dp.util.HttpUtil;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class LockerAuthServiceImpl implements LockerAuthService {

    @Autowired
    AuthTokenUtil authTokenUtil;

    @Override
    public boolean userAuthNotify(PatientAuth payload) throws Exception {
        Boolean notify = false;
        String url = "https://dev.ndhm.gov.in/gateway/v0.5/users/auth/notify";
        Map<String, String> headers = new HashMap<>();
        headers.put("X-HIU-ID", "DPHL119_01");
        headers.put("Authorization", this.authTokenUtil.bearerAuthToken());
        //prepare payload
        payload.getValidity().setRequester(new PatientAuthRequester("HEALTH_LOCKER","DPHL119_01"));
        PatientAuthNotification patientAuthNotification = new PatientAuthNotification(UUID.randomUUID().toString(), Instant.now().toString(),payload);
        String jsonPayload = new Gson().toJson(patientAuthNotification);
        Map<String, Object> response = HttpUtil.post(url, jsonPayload, headers);
        if(ObjectUtils.isNotEmpty(response) && response.get("status").equals(202))
            notify = true;
        return notify;
    }

    @Override
    public boolean authNotifyCallBack(Map<String, Object> payload) throws Exception {
        Boolean notify = false;
        String url = "https://dev.ndhm.gov.in/gateway/v0.5/users/auth/on-notify";
        Map<String, String> headers = new HashMap<>();
        headers.put("X-CM-ID", "sbx");
        headers.put("Authorization", this.authTokenUtil.bearerAuthToken());
        //prepare payload
        Map<String,Object> apiPayload = new HashMap<>();
        apiPayload.put("requestId",UUID.randomUUID().toString());
        apiPayload.put("timestamp",Instant.now().toString());

        Map<String,Object> ackMap = new HashMap<>();
        ackMap.put("status","OK");//
        apiPayload.put("acknowledgement",ackMap);
        Map<String,Object> respMap = new HashMap<>();
        respMap.put("requestId",(String)payload.get("requestId"));
        apiPayload.put("resp",respMap);
        if(ObjectUtils.isNotEmpty(payload.get("error"))){
            apiPayload.put("error",payload.get("error"));
        }
        String jsonPayload = new Gson().toJson(apiPayload);
        Map<String, Object> response = HttpUtil.post(url, jsonPayload, headers);
        if(ObjectUtils.isNotEmpty(response) && response.get("status").equals(202))
            notify = true;
        return notify;
    }
}
