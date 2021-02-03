package com.pb.dp.healthIdCreation.service;


import com.pb.dp.healthIdCreation.model.CustomerDetails;
import com.pb.dp.healthIdCreation.model.MobileOtpPojo;

import java.text.ParseException;
import java.util.Map;

public interface HealthIdService {
    Map<String, Object> registerViaMobile(CustomerDetails customerDetail) throws ParseException, Exception;

    Map<String, Object> verifyViaMobile(MobileOtpPojo mobileOtpPojo, Integer custId) throws Exception;
}
