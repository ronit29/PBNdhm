package com.policybazaar.docprimNdhm.common.service;


import com.policybazaar.docprimNdhm.common.model.AuthDetail;
import com.policybazaar.docprimNdhm.common.model.OAuthUrlInfo;
import com.policybazaar.docprimNdhm.common.model.PropertyConfigModel;
import com.policybazaar.docprimNdhm.encryption.AES256Cipher;

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
