package com.pb.dp.healthIdCreation.dao;


import com.pb.dp.healthIdCreation.model.Customer;
import com.pb.dp.healthIdCreation.model.CustomerDetails;
import com.pb.dp.healthIdCreation.model.MobileOtpPojo;

public interface HealthIdDao {
    void addCustomer(CustomerDetails customerDetail) throws Exception;

    void addNdhmOtpTxnId(long mobileNo, String txnId);

    void updateNdhmOTP(MobileOtpPojo mobileOtpPojo);

    void updateNdhmOtpToken(MobileOtpPojo mobileOtpPojo);

    Customer getCustomer(Integer custId, Long mobile);
}
