package com.pb.dp.service;

import com.pb.dp.model.PatientAuth;

import java.util.Map;

public interface LockerAuthService {
    boolean userAuthNotify(PatientAuth payload) throws Exception;

    boolean authNotifyCallBack(Map<String, Object> payload) throws Exception;
}
