package com.pb.dp.service;


import com.pb.dp.model.CustomerDetails;
import com.pb.dp.model.NdhmMobOtpRequest;
import com.pb.dp.model.RegisterAadharRequest;
import com.pb.dp.model.VerifyOtpWithAadharRequest;

import java.text.ParseException;
import java.util.Map;

public interface HealthIdService {
    Map<String, Object> registerViaMobile(CustomerDetails customerDetail, int customerId) throws ParseException, Exception;

    Map<String, Object> verifyForRegistration(NdhmMobOtpRequest ndhmMobOtpRequest, CustomerDetails customerProfileData, Integer custId) throws Exception;

    Map<String, Object> resendNdhmOtp(String txnId) throws Exception;

    Map<String, Object> updateHealthIdProfile(NdhmMobOtpRequest ndhmMobOtpRequest, CustomerDetails customerProfileData, int customerId) throws Exception;

    Map<String, Object> generateOtpForUpdate(CustomerDetails customerDetails, int customerId) throws Exception;

	Map<String, Object> deleteHealthId(String healthId) throws Exception;

	Map<String, Object> deleteHealthId(NdhmMobOtpRequest ndhmMobOtpRequest) throws Exception;

	Map<String, Object> registerWithAadhar(RegisterAadharRequest registerAadharRequest) throws Exception;

	Map<String, Object> verifyOtpWithAadhar(VerifyOtpWithAadharRequest verifyOtpWithAadharRequest)throws Exception;
}
