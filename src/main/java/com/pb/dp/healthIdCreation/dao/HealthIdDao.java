package com.pb.dp.healthIdCreation.dao;


import com.pb.dp.healthIdCreation.model.Customer;
import com.pb.dp.healthIdCreation.model.CustomerDetails;
import com.pb.dp.healthIdCreation.model.HealthId;
import com.pb.dp.healthIdCreation.model.NdhmMobOtpRequest;

import java.text.ParseException;

public interface HealthIdDao {
    Integer addHealthIdDemographics(CustomerDetails customerDetail, int customerId) throws Exception;

    void addNdhmOtpTxnId(long mobileNo, String txnId) throws Exception;

    void updateNdhmOTP(NdhmMobOtpRequest ndhmMobOtpRequest) throws Exception;

    void updateNdhmOtpToken(NdhmMobOtpRequest ndhmMobOtpRequest, Integer custId) throws Exception;

    CustomerDetails getCustomerDetails(Integer custId, Long mobile, String txnId) throws Exception;

    void updateNdhmTxnId(Integer custId, String txnId) throws Exception;

    Long addNewCustomer(Long mobile, int otp) throws Exception;

    void updateProfileData(CustomerDetails customerDetails, int customerId) throws ParseException;

    void addHealthIdData(CustomerDetails customerDetails, int customerId,String txnId) throws Exception;

    Customer getCustomerByMobile(Long mobile) throws Exception;

    void updateCustomer(Long mobile, int otp, long id) throws Exception;

    HealthId getHealthIdDetails(int customerId, Integer valueOf) throws Exception;

    void updateProfileTxnId(int customerId, String healthId, String txnId);

    HealthId getByHealth(String healthId);

    void deleteHealthIdData(Integer customerId, String txnId);
}
