package com.pb.dp.dao;


import com.pb.dp.model.Customer;
import com.pb.dp.model.CustomerDetails;
import com.pb.dp.model.HealthId;
import java.text.ParseException;

public interface HealthIdDao {
    Integer addHealthIdDemographics(CustomerDetails customerDetail, int customerId) throws Exception;

    Long addNewCustomer(Long mobile, int otp) throws Exception;

    void updateProfileData(CustomerDetails customerDetails, int customerId) throws ParseException;

    Customer getCustomerByMobile(Long mobile) throws Exception;

    void updateCustomer(Long mobile, int otp, long id) throws Exception;

    HealthId getHealthIdDetails(int customerId, Integer valueOf) throws Exception;

    HealthId getByHealth(String healthId);
}
