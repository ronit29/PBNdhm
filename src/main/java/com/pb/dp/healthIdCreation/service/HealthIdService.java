package com.pb.dp.healthIdCreation.service;


import com.pb.dp.healthIdCreation.model.CustomerDetails;
import com.pb.dp.healthIdCreation.model.NdhmMobOtpRequest;

import java.text.ParseException;
import java.util.Map;

public interface HealthIdService {
    Map<String, Object> registerViaMobile(CustomerDetails customerDetail, int customerId) throws ParseException, Exception;

    Map<String, Object> verifyForRegistration(NdhmMobOtpRequest ndhmMobOtpRequest, Integer custId) throws Exception;

    Map<String, Object> resendNdhmOtp(String txnId) throws Exception;

    Map<String, Object> updateHealthIdProfile(NdhmMobOtpRequest ndhmMobOtpRequest, int customerId) throws Exception;

    Map<String, Object> generateOtpForUpdate(CustomerDetails customerDetails, int customerId) throws Exception;
}
