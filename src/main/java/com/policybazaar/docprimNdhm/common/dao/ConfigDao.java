package com.policybazaar.docprimNdhm.common.dao;

import com.policybazaar.docprimNdhm.common.model.AuthDetail;
import com.policybazaar.docprimNdhm.common.model.OAuthUrlInfo;
import com.policybazaar.docprimNdhm.common.model.PropertyConfigModel;

import java.util.List;

public interface ConfigDao {

    List<AuthDetail> getAuthDetails();

    List<OAuthUrlInfo> getServiceUrls();

    List<PropertyConfigModel> getPropertyConfigs();

}
