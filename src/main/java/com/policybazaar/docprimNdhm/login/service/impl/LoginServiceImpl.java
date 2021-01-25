package com.policybazaar.docprimNdhm.login.service.impl;

import com.policybazaar.docprimNdhm.common.enums.ResponseStatus;
import com.policybazaar.docprimNdhm.common.model.FieldKey;
import com.policybazaar.docprimNdhm.common.service.ConfigService;
import com.policybazaar.docprimNdhm.login.dao.LoginDao;
import com.policybazaar.docprimNdhm.login.service.LoginService;
import com.policybazaar.docprimNdhm.util.CommunicationServiceUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    CommunicationServiceUtil communicationServiceUtil;

    @Autowired
    LoginDao loginDao;

    @Autowired
    ConfigService configService;

    @Override
    public Map<String, Object> sendOtp(Long mobile) throws Exception {
        Map<String, Object> response = new HashMap<>();
            Integer result = 0;
            Integer countryCode = 91;
             int otp = (int) (Math.random() * 9999);
            if (otp <= 1000) {
                otp += 1000;
            }
            String smsResponse = "";
            String message = null;
            int smsType = 0;
            String smsTemplate = this.configService.getPropertyConfig("otp.sms.template").getValue();
            //Todo once customer detail tables are finalised, take name from there
            //String name = otpDao.getCustomerName(mobile);
            if(!StringUtils.isEmpty(smsTemplate)) {
                message = message.replace("@Name", "user").replace("@OTP", String.valueOf(otp));
            }
            Map<String,Object> sendSmsResp = null;
            sendSmsResp = this.communicationServiceUtil.sendSms(countryCode.toString(), mobile.toString(), message, true);
            String uuid = null;
            if(Objects.nonNull(sendSmsResp)) {
                smsResponse = "OK";
                uuid = String.valueOf(sendSmsResp.get("Description"));
                result = loginDao.inserOtpDetails(otp, countryCode, mobile, message, smsResponse, smsType, uuid);
            }

        if (result == 1) {
            response.put(FieldKey.SK_STATUS_MESSAGE, ResponseStatus.SUCCESS.getStatusMsg());
            response.put(FieldKey.SK_STATUS_CODE, ResponseStatus.SUCCESS.getStatusId());
        } else {
            response.put(FieldKey.SK_STATUS_MESSAGE, ResponseStatus.FAILURE.getStatusMsg());
            response.put(FieldKey.SK_STATUS_CODE, ResponseStatus.FAILURE.getStatusId());
        }
        return response;
     }
}
