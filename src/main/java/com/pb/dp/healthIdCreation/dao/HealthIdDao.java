package com.pb.dp.healthIdCreation.dao;


import com.pb.dp.healthIdCreation.model.CustomerDetails;
import com.pb.dp.healthIdCreation.model.NdhmMobOtpRequest;

import java.text.ParseException;

public interface HealthIdDao {
    Integer addCustomer(CustomerDetails customerDetail, int customerId) throws Exception;

    void addNdhmOtpTxnId(long mobileNo, String txnId) throws Exception;

    void updateNdhmOTP(NdhmMobOtpRequest ndhmMobOtpRequest) throws Exception;

    void updateNdhmOtpToken(NdhmMobOtpRequest ndhmMobOtpRequest, Integer custId) throws Exception;

    CustomerDetails getCustomerDetails(Integer custId) throws Exception;

    void updateNdhmTxnId(Integer custId, String txnId) throws Exception;

    Integer addNewCustomer(Long mobile, int otp) throws Exception;

    void updateProfileData(CustomerDetails customerDetails, int customerId) throws ParseException;

    void addHealthIdData(CustomerDetails customerDetails, int customerId) throws Exception;
}
