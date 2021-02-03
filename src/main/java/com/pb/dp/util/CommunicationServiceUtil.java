package com.pb.dp.util;

import com.google.gson.Gson;
import com.pb.dp.service.ConfigService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class CommunicationServiceUtil {

    private static final Logger logger = LoggerFactory.getLogger(CommunicationServiceUtil.class);

    @Autowired
    private ConfigService configService;

    private static Integer defaultCustomerId = -99;

    private static Integer defaultLeadId = 0;

    private static Integer defaultProductId = 0;

    private static String defaultTrigger = "CS Email";

    public Map<String, Object> sendSms(String countryCode, String mobileNo, String message, Boolean nonQueue) throws Exception {
        String jsonPayload = prepareSmsPayload(countryCode, mobileNo, message, nonQueue);
        Map<String, Object> responseMap = null;
        Map<String, Object> sendResultMap = null;
        if (nonQueue) {
            Map<String, String> headers = new HashMap<String, String>();
            headers.put("REQUESTINGSYSTEM", configService.getPropertyConfig("commbox.send.requestingSystem").getValue());
            headers.put("TOKEN", configService.getPropertyConfig("commbox.send.token").getValue());
                responseMap = HttpUtil.post(configService.getPropertyConfig("commbox.send.url").getValue(), jsonPayload, headers);
        } else {
            responseMap = HttpUtil.post(configService.getPropertyConfig("commbox.send.url").getValue(), jsonPayload);
        }
        if(Objects.nonNull(responseMap)) {
            Object responseBody = responseMap.get("responseBody");
            if(Objects.nonNull(responseBody) && responseMap.get("status").equals(200)) {
                Map<String, Object> responseBodyMap = new Gson().fromJson(responseBody.toString(), Map.class);
                Object sendResult = responseBodyMap.get("SendResult");
                if(Objects.nonNull(sendResult)) {
                    sendResultMap = new Gson().fromJson(sendResult.toString(), Map.class);
                }
            }
        }
        return sendResultMap;
    }

    private String prepareSmsPayload(String countryCode, String mobile, String message, Boolean nonQueue) {
        return prepareSmsPayload(countryCode, mobile, message, null, null, null, 0, nonQueue);
    }

    private String prepareSmsPayload(String countryCode, String mobile, String message, Integer customerId, Integer leadId, Integer productId, Integer isBooked, Boolean nonQueue) {
        Map<String, Object> commboxPayload = new HashMap<>();
        Map<String, Object> communicationDetails = new HashMap<>();
        communicationDetails.put("CustID", customerId == null ? defaultCustomerId : customerId);
        communicationDetails.put("LeadID", leadId == null ? defaultLeadId : leadId);
        communicationDetails.put("ProductID", productId == null ? defaultProductId : productId);
        communicationDetails.put("CommunicationType", 2); // sms
        communicationDetails.put("IsBooking", isBooked);
        List<Map<String, Object>> conversations = new ArrayList<>();
        Map<String, Object> conversation = new HashMap<>();
        List<String> toReceipent = new ArrayList<>();
        toReceipent.add(mobile);
        conversation.put("ToReceipent", toReceipent);
        conversation.put("Body", message);
        conversation.put("CreatedBy", "CS");
        conversations.add(conversation);
        if (nonQueue) {
            conversation.put("TriggerName", configService.getPropertyConfig("commbox.sms.trigger").getValue());
            conversation.put("DisplaySenderName", "POLBAZ");
            conversation.put("CountryCode", countryCode == null ? "91" : countryCode);
        }
        communicationDetails.put("Conversations", conversations);
        commboxPayload.put("CommunicationDetails", communicationDetails);
        Gson gson = new Gson();
        return gson.toJson(commboxPayload);
    }

    public Map<String, Object> sendEmail(Map<String, Object> inputDetails) throws Exception {

        Map<String, String> headers = new HashMap<>();
        headers.put("TOKEN", configService.getPropertyConfig("commbox.send.token").getValue());
        headers.put("REQUESTINGSYSTEM", configService.getPropertyConfig("commbox.send.requestingSystem").getValue());
        headers.put("Content-Type", "application/json");
//        if (ifInternationalProduct(AppUtil.getInt(inputDetails.get("productId")))) {
//            inputDetails.put("triggerName", CommboxTrigger.UAE_CUSTOMER.getTrigger());
//        } else {
//            inputDetails.put("triggerName", CommboxTrigger.CUSTOMER.getTrigger());
//        }
        inputDetails.put("triggerName", configService.getPropertyConfig("commbox.email.trigger").getValue());
        String jsonPayload = prepareEmailPayload(inputDetails);
        String timeStamp = String.valueOf(new Date());
        Map<String, Object> responseMap = HttpUtil.post(configService.getPropertyConfig("commbox.send.url").getValue(), jsonPayload, headers);
        return prepareEmailResponse(responseMap);
    }

    private String prepareEmailPayload(Map<String, Object> inputDetails) {

        Map<String, Object> commboxPayload = new HashMap<>();
        Map<String, Object> communicationDetails = new HashMap<>();
        communicationDetails.put("CustID", inputDetails.get("customerId") == null ? defaultCustomerId : Long.valueOf(inputDetails.get("customerId").toString()));
        communicationDetails.put("LeadID", inputDetails.get("leadId") == null ? defaultLeadId : Long.valueOf(inputDetails.get("leadId").toString()));
        communicationDetails.put("ProductID", inputDetails.get("productId") == null ? defaultProductId : Integer.valueOf(inputDetails.get("productId").toString()));
        communicationDetails.put("CommunicationType", 1); // email
        //todo ask Ayush for isBooking
        //communicationDetails.put("IsBooking", inputDetails.get("isBooking") == null ? 0 : AppUtil.getInt(inputDetails.get("isBooking")));
        communicationDetails.put("IsBooking", 0);
        List<Map<String, Object>> conversations = new ArrayList<>();
        Map<String, Object> conversation = new HashMap<>();
        String[] to = String.valueOf(inputDetails.get("receipient")).split(",");
        String[] bcc = new String[0];
        String[] cc = new String[0];
        if (null != inputDetails && inputDetails.get("bccEmailId") != null && !String.valueOf(inputDetails.get("bccEmailId")).isEmpty()) {
            bcc = String.valueOf(inputDetails.get("bccEmailId")).split(",");
        }
        conversation.put("BccEmail", bcc);
        if (null != inputDetails && inputDetails.get("ccEmailId") != null && !String.valueOf(inputDetails.get("ccEmailId")).isEmpty()) {
            cc = String.valueOf(inputDetails.get("ccEmailId")).split(",");
        }
        conversation.put("CCEmail", cc);
        conversation.put("From", inputDetails.get("sender"));
        conversation.put("ToReceipent", to);
        conversation.put("Body", inputDetails.get("message"));
        conversation.put("Subject", inputDetails.get("subject") != null ? inputDetails.get("subject") : defaultTrigger);
        conversation.put("CreatedBy", "CS");
        conversation.put("TriggerName", inputDetails.get("triggerName") == null ? defaultTrigger : inputDetails.get("triggerName"));
        conversation.put("AutoTemplate", true);
        conversation.put("DisplaySenderName", "POLBAZ");
        conversations.add(conversation);

        communicationDetails.put("Conversations", conversations);
        commboxPayload.put("CommunicationDetails", communicationDetails);
        Gson gson = new Gson();
        logger.debug(gson.toJson(commboxPayload));
        return gson.toJson(commboxPayload);
    }

    private Map<String, Object> prepareEmailResponse(Map<String, Object> res) {
        Map<String, Object> responseBodyJson = null;
        int ok = 0;
        String uniqueId = "";
        try {
            if (res != null && res.size() > 0) {
                String responseBody = String.valueOf(res.get("responseBody"));
                //responseBodyJson = (Map<String, Object>) JSON.parse(responseBody);
                if (responseBodyJson != null) {
                    if (null != responseBodyJson.get("SendResult")) {
                        Map<String, Object> result = (Map<String, Object>) responseBodyJson.get("SendResult");
                        uniqueId = String.valueOf(result.get("Description"));
                        ok = 1;
                    }
                    responseBodyJson.put("msg", "success");
                }
            } else {
                responseBodyJson = new HashMap<>();
                responseBodyJson.put("msg", "Fail");
            }
            if (null != responseBodyJson) {
                responseBodyJson.put("ok", ok);
//                responseBodyJson.put(CommboxApiPayloadParser.UUID, uniqueId);
                responseBodyJson.put("UUID", uniqueId);
            }

        } catch (Exception e) {
            responseBodyJson = new HashMap<>();
            logger.error("exception occured while preparing email response for data:{}", res);
            responseBodyJson.put("ok", ok);
//            responseBodyJson.put(CommboxApiPayloadParser.UUID, uniqueId);
            responseBodyJson.put("UUID", uniqueId);
        }
        return responseBodyJson;
    }
}
