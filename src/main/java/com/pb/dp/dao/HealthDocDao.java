package com.pb.dp.dao;

import java.util.List;

public interface HealthDocDao {

	List<String> getDocOwners(int customerId);

}
