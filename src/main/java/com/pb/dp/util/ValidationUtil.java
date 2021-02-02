package com.pb.dp.util;
//package com.policybazaar.docprimNdhm.common.util;
//
//import com.policybazaar.coreservice.common.service.ConfigService;
//import com.policybazaar.coreservice.enums.ResponseStatus;
//import com.policybazaar.coreservice.model.AuthDetail;
//import com.policybazaar.coreservice.model.FieldKey;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import java.util.HashMap;
//import java.util.Map;
//
//@Component
//public class ValidationUtil {
//    @Autowired
//    private ConfigService configService;
//
//    public Map<String, Object> validateClientAndAuthKey(String clientKey, String authKey) {
//        Map<String, Object> response = null;
//        AuthDetail authDetail = configService.getAuthDetail(clientKey);
//        if (null == clientKey || null == authDetail) {
//            response = new HashMap<>();
//            response.put(FieldKey.SK_STATUS_MESSAGE, ResponseStatus.INVALID_CLIENT_KEY.getStatusMsg());
//            response.put(FieldKey.SK_STATUS_CODE, ResponseStatus.INVALID_CLIENT_KEY.getStatusId());
//            return response;
//        }
//        if (!authDetail.getAuth_key().equals(authKey)) {
//            response = new HashMap<>();
//            response.put(FieldKey.SK_STATUS_MESSAGE, ResponseStatus.INVALID_AUTH_KEY.getStatusMsg());
//            response.put(FieldKey.SK_STATUS_CODE, ResponseStatus.INVALID_AUTH_KEY.getStatusId());
//        }
//        return response;
//    }
//
//}
