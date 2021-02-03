package com.pb.dp.util;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pb.dp.enums.ResponseStatus;
import com.pb.dp.model.AuthDetail;
import com.pb.dp.model.FieldKey;
import com.pb.dp.service.ConfigService;

import java.util.HashMap;
import java.util.Map;

@Component
public class ValidationUtil {
    @Autowired
    private ConfigService configService;

    public Map<String, Object> validateClientAndAuthKey(String clientKey, String authKey) {
        Map<String, Object> response = null;
        AuthDetail authDetail = configService.getAuthDetail(clientKey);
        if (null == clientKey || null == authDetail) {
            response = new HashMap<>();
            response.put(FieldKey.SK_STATUS_MESSAGE, ResponseStatus.INVALID_CLIENT_KEY.getStatusMsg());
            response.put(FieldKey.SK_STATUS_CODE, ResponseStatus.INVALID_CLIENT_KEY.getStatusId());
            return response;
        }
        if (!authDetail.getAuth_key().equals(authKey)) {
            response = new HashMap<>();
            response.put(FieldKey.SK_STATUS_MESSAGE, ResponseStatus.INVALID_AUTH_KEY.getStatusMsg());
            response.put(FieldKey.SK_STATUS_CODE, ResponseStatus.INVALID_AUTH_KEY.getStatusId());
        }
        return response;
    }

}
