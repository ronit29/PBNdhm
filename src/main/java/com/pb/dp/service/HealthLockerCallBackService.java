package com.pb.dp.service;

import java.util.Map;

public interface HealthLockerCallBackService {

	boolean updateCallBackSubscribe(Map<String, Object> payload) throws Exception;

	boolean updateCallBackAuth(Map<String, Object> payload) throws Exception;

	boolean updateCallBackSubscribeOnNotify(Map<String, Object> payload) throws Exception;

}
