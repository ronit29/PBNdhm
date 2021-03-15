package com.pb.dp.dao;

public interface HealthLockerCallBackDao {

	boolean updateCallBackSubscribe(String requestId, String subscriptionId, String reqIdSent) throws Exception;

}
