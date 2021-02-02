package com.pb.dp.service;


import com.pb.dp.model.AuthDetail;
import com.pb.dp.model.OAuthUrlInfo;
import com.pb.dp.model.PropertyConfigModel;
import com.pb.dp.util.AES256Cipher;

import java.util.Map;

public interface ConfigService {
    void refreshMaps();

    AuthDetail getAuthDetail(String clientKey);

    Map<String, AuthDetail> getAuthDetails();

    OAuthUrlInfo getServiceUrl(String url);

    PropertyConfigModel getPropertyConfig(String key);



    AES256Cipher getAESForClientKeyMap(String clientKey);

    String getSourceForProductId(int productId);

//    List<String> getWhitelistedClientKey();
}
