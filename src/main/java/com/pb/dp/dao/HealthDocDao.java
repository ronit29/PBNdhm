package com.pb.dp.dao;

import com.pb.dp.model.CustomerDetails;
import com.pb.dp.model.HealthDoc;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

public interface HealthDocDao {

	List<String> getDocOwners(int customerId);
	boolean uploadDocs(HealthDoc healthDoc, int customerId);
}
