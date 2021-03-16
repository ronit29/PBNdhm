package com.pb.dp.dao;

import com.pb.dp.model.Authorise;
import com.pb.dp.model.Subscribe;

public interface HealthLockerDao {

	void insertIntoSubscribe(Subscribe subscribe) throws Exception;

	void insertIntoAuth(Authorise authorise) throws Exception;

}
