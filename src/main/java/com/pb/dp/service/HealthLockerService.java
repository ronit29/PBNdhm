package com.pb.dp.service;

import com.pb.dp.model.LockerModel;

public interface HealthLockerService {

	boolean authorize(LockerModel lockerModel) throws Exception;

	boolean subscribe(LockerModel lockerModel) throws Exception;

}
