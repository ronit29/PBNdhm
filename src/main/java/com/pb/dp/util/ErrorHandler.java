package com.pb.dp.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pb.dp.enums.ResponseStatus;
import com.pb.dp.model.FieldKey;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ErrorHandler {

    public Map<String,Object> ndhmErrorHandler(Map<String,Object> responseBodyMap, Integer status){
        Map<String,Object> response = new HashMap<>();
        response.put("ndhmStatus",status);
        response.put(FieldKey.SK_STATUS_CODE, ResponseStatus.NDHM_FAILURE.getStatusId());
        String errorMsg = (String)responseBodyMap.get("message");
        response.put(FieldKey.SK_STATUS_MESSAGE, ResponseStatus.NDHM_FAILURE.getStatusMsg() + " :" + errorMsg);
        if(ObjectUtils.isNotEmpty(responseBodyMap.get("details"))) {
            ObjectMapper mapper = new ObjectMapper();
            List<Map<String, Object>> errorDetails = mapper.convertValue(responseBodyMap.get("details"), List.class);
            response.put("ErrorDetails", errorDetails);
        }
        return response;
    }
}
