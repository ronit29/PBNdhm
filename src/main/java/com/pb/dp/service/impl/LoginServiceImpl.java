package com.pb.dp.service.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.pb.dp.dao.HealthIdDao;
import com.pb.dp.model.Customer;
import com.pb.dp.service.ConfigService;
import com.pb.dp.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.pb.dp.dao.LoginDao;
import com.pb.dp.enums.ResponseStatus;
import com.pb.dp.model.FieldKey;
import com.pb.dp.util.CommunicationServiceUtil;

@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    CommunicationServiceUtil communicationServiceUtil;

    @Autowired
    LoginDao loginDao;

    @Autowired
    HealthIdDao healthIdDao;

    @Autowired
    ConfigService configService;

    @Override
    public Map<String, Object> sendOtp(Long mobile) throws Exception {
        Map<String, Object> response = new HashMap<>();
            Integer result = 0;
            Long custId =0l;
            Integer countryCode = 91;
             int otp = (int) (Math.random() * 9999);
            if (otp <= 1000) {
                otp += 1000;
            }
            String smsResponse = "";
            String message = null;
            int smsType = 0;
            String smsTemplate = this.configService.getPropertyConfig("otp.sms.template").getValue();
           // String name = loginDao.getCustomerName(mobile);
            if(!ObjectUtils.isEmpty(smsTemplate)) {
                message = smsTemplate.replace("@Name", "user").replace("@OTP", String.valueOf(otp));
            }
            Map<String,Object> sendSmsResp = null;
            sendSmsResp = communicationServiceUtil.sendSms(countryCode.toString(), mobile.toString(), message, true);
            String uuid = null;
            if(Objects.nonNull(sendSmsResp)) {
                smsResponse = "OK";
                uuid = String.valueOf(sendSmsResp.get("Description"));
                result = loginDao.inserOtpDetails(otp, countryCode, mobile, message, smsResponse, smsType, uuid);
                custId = this.addNewCustomer(mobile,otp);


            }
        if (custId > 0) {
            response.put("mobileNo",mobile);
            response.put("custId",custId);
            response.put(FieldKey.SK_STATUS_MESSAGE, ResponseStatus.SUCCESS.getStatusMsg());
            response.put(FieldKey.SK_STATUS_CODE, ResponseStatus.SUCCESS.getStatusId());
        } else {
            response.put(FieldKey.SK_STATUS_MESSAGE, ResponseStatus.FAILURE.getStatusMsg());
            response.put(FieldKey.SK_STATUS_CODE, ResponseStatus.FAILURE.getStatusId());
        }
        return response;
     }

    private Long addNewCustomer(Long mobile, int otp) throws Exception {
        Customer customer = this.healthIdDao.getCustomerByMobile(mobile);
        if(ObjectUtils.isEmpty(customer)) {
            Long custId = this.healthIdDao.addNewCustomer(mobile, otp);
            return custId;
        } else {
            this.healthIdDao.updateCustomer(mobile,otp,customer.getId());
            return customer.getId();
        }
    }
}
