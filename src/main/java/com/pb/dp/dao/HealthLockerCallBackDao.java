package com.pb.dp.dao;

public interface HealthLockerCallBackDao {

	boolean updateCallBackSubscribe(String requestId, String subscriptionId, String reqIdSent) throws Exception;

	boolean updateCallBackAuth(String requestId, String transactionId, String reqIdSent) throws Exception;

	boolean updateCallBackAuthNotify(String transactionId, String accessToken, String hiuId) throws Exception;

}
