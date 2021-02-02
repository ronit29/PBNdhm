package com.pb.dp.service;


import com.pb.dp.dao.ConfigDao;
import com.pb.dp.model.AuthDetail;
import com.pb.dp.model.OAuthUrlInfo;
import com.pb.dp.model.PropertyConfigModel;
import com.pb.dp.util.AES256Cipher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service("configService")
public class ConfigServiceImpl implements ConfigService {

    private static Map<String, AuthDetail> authDetailMap;
    private static Map<String, OAuthUrlInfo> serviceUrlMap;
    private static Map<String, PropertyConfigModel> propertyConfigMap;
    private static Map<String, AES256Cipher> aesForClientKeyMap;
    private static Map<Integer, String> productIdSourceMap;

    @Autowired
    private ConfigDao configDao;

    @PostConstruct
    public void init() {
        refreshMaps();
    }

    @Override
    public void refreshMaps() {
        setAuthDetailMap();
        setServiceUrlMap();
        setPropertyConfigMap();
        setAesForClientKeyMap();
    }

    @Override
    public AuthDetail getAuthDetail(String clientKey) {
        return authDetailMap.get(clientKey);
    }

    @Override
    public Map<String, AuthDetail> getAuthDetails() {
        return authDetailMap;
    }

    private void setAuthDetailMap() {
        Map<String, AuthDetail> authDetailMap = new HashMap<>();
        Map<Integer, String> productIdSourceMap = new HashMap<>();
        for (AuthDetail authDetail : configDao.getAuthDetails()) {
            authDetailMap.put(authDetail.getHash_client_key(), authDetail);
            productIdSourceMap.put(authDetail.getProduct_id(), authDetail.getSource());
        }
        ConfigServiceImpl.authDetailMap = authDetailMap;
        ConfigServiceImpl.productIdSourceMap = productIdSourceMap;
    }

    @Override
    public OAuthUrlInfo getServiceUrl(String url) {
        OAuthUrlInfo oAuthUrlInfo = serviceUrlMap.get(url);
        if (oAuthUrlInfo == null) {
            Optional<String> matchedUrl = serviceUrlMap.keySet().parallelStream().filter(url::matches).findFirst();
            if (matchedUrl.isPresent())
                oAuthUrlInfo = serviceUrlMap.get(matchedUrl.get());
        }
        return oAuthUrlInfo;
    }

    private void setServiceUrlMap() {
        Map<String, OAuthUrlInfo> serviceUrlMap = new HashMap<>();
        for (OAuthUrlInfo oAuthUrlInfo : configDao.getServiceUrls())
            serviceUrlMap.put(oAuthUrlInfo.getUrl().replaceAll("[{]+[A-Za-z]*+[}]", "[a-zA-Z0-9]*"), oAuthUrlInfo);
        ConfigServiceImpl.serviceUrlMap = serviceUrlMap;
    }

    @Override
    public PropertyConfigModel getPropertyConfig(String key) {
        return propertyConfigMap.get(key);
    }

    private void setPropertyConfigMap() {
        Map<String, PropertyConfigModel> propertyConfigMap = new HashMap<>();
        for (PropertyConfigModel propertyConfigModel : configDao.getPropertyConfigs())
            propertyConfigMap.put(propertyConfigModel.getKey(), propertyConfigModel);
        ConfigServiceImpl.propertyConfigMap = propertyConfigMap;
    }

    @Override
    public AES256Cipher getAESForClientKeyMap(String clientKey) {
        return aesForClientKeyMap.get(clientKey);
    }

    private void setAesForClientKeyMap() {
        Map<String, AES256Cipher> aesForClientKeyMap = new HashMap<>();
        for (Map.Entry<String, AuthDetail> authDetail : ConfigServiceImpl.authDetailMap.entrySet()) {
            AuthDetail detail = authDetail.getValue();
            if (detail.isIs_encryption_enabled() && null != detail.getEncryption_key() && null != detail.getInit_vector()) {
                aesForClientKeyMap.put(detail.getHash_client_key(), new AES256Cipher(detail.getEncryption_key(), detail.getInit_vector()));
            }
        }
        ConfigServiceImpl.aesForClientKeyMap = aesForClientKeyMap;
    }

    @Override
    public String getSourceForProductId(int productId) {
        return productIdSourceMap.get(productId);
    }

//    @Override
//    public List<String> getWhitelistedClientKey() {
//        List<String> validClientKeyList;
//        PropertyConfigModel validProperty = propertyConfigMap.get(AppConstant.CLIENT_KEY);
//        String validClientKeys = validProperty.getValue();
//        validClientKeyList = Arrays.asList(validClientKeys.split("\\s*,\\s*"));
//        return validClientKeyList;
//    }
}