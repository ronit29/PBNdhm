package com.pb.dp.healthIdCreation.dao;


import com.pb.dp.healthIdCreation.model.Customer;
import com.pb.dp.healthIdCreation.model.CustomerDetails;
import com.pb.dp.healthIdCreation.model.NdhmMobOtpRequest;

public interface HealthIdDao {
    Integer addCustomer(CustomerDetails customerDetail, int customerId) throws Exception;

    void addNdhmOtpTxnId(long mobileNo, String txnId);

    void updateNdhmOTP(NdhmMobOtpRequest ndhmMobOtpRequest);

    void updateNdhmOtpToken(NdhmMobOtpRequest ndhmMobOtpRequest, Integer custId) throws Exception;

    Customer getCustomer(Integer custId, Long mobile);

    void updateNdhmTxnId(Integer custId, String txnId);
}
