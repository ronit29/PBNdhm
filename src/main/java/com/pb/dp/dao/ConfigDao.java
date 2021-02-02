package com.pb.dp.dao;

import java.util.List;

import com.pb.dp.model.AuthDetail;
import com.pb.dp.model.OAuthUrlInfo;
import com.pb.dp.model.PropertyConfigModel;

public interface ConfigDao {

    List<AuthDetail> getAuthDetails();

    List<OAuthUrlInfo> getServiceUrls();

    List<PropertyConfigModel> getPropertyConfigs();

}
