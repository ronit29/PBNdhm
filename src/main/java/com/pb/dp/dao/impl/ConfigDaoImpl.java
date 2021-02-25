package com.pb.dp.dao.impl;


import com.pb.dp.dao.ConfigDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.pb.dp.model.AuthDetail;
import com.pb.dp.model.OAuthUrlInfo;
import com.pb.dp.model.PropertyConfigModel;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.util.List;

@Repository("configDao")
public class ConfigDaoImpl implements ConfigDao {
    private static final Logger logger = LoggerFactory.getLogger(ConfigDaoImpl.class);

    private final String SQL_GET_ALL_SERVICE_URLS = "select * from m_service_url msu (nolock) where msu.isActive = 1 ";
    private final String SQL_GET_ALL_AUTH_DETAILS = "select * from m_auth_details mad (nolock) where mad.is_enabled = 1 ";
    private final String SQL_GET_ALL_PROPERTY_CONFIGS = "select * from m_properties_config mpc (nolock) where mpc.isActive = 1 ";

    @Autowired
    private DataSource dataSource;

    private JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void setJdbcTemplate() {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public List<AuthDetail> getAuthDetails() {
        return jdbcTemplate.query(SQL_GET_ALL_AUTH_DETAILS, new AuthDetail.AuthDetailMapper());
    }

    @Override
    public List<OAuthUrlInfo> getServiceUrls() {
        return jdbcTemplate.query(SQL_GET_ALL_SERVICE_URLS, new OAuthUrlInfo.OAuthUrlInfoMapper());
    }

    @Override
    public List<PropertyConfigModel> getPropertyConfigs() {
        return jdbcTemplate.query(SQL_GET_ALL_PROPERTY_CONFIGS, new PropertyConfigModel.PropertyConfigModelMapper());
    }


}