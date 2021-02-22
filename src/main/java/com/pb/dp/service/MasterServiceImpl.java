package com.pb.dp.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.pb.dp.healthIdCreation.dao.HealthIdDao;
import com.pb.dp.healthIdCreation.model.HealthId;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pb.dp.dao.MasterDao;

/**
 * This class implements service layer for master
 * 
 * @author Aditya Rathore
 *
 */
@Service("masterService")
public class MasterServiceImpl implements MasterService {

	@Autowired
	private MasterDao masterDao;

	@Autowired
	private HealthIdDao healthIdDao;

	
	@Override
	public List<Map<String,Object>> getState() throws Exception{
		return masterDao.getState();
	}

	@Override
	public List<Map<String, Object>> getDistrictsForState(Integer stateCode) throws Exception {
		return masterDao.getDistrictsForState(stateCode);
	}
	
	@Override
	public List<Map<String, Object>> getRelations(int customerId) throws Exception {
		List<Map<String, Object>> relationsList = masterDao.getRelations();
		Map<String, Object> selfMap = relationsList
				.stream()
				.filter(e -> ((String) e.get("name")).equalsIgnoreCase("Self"))
				.findAny()
				.orElse(null);
		if (ObjectUtils.isNotEmpty(selfMap)) {
			HealthId healthId = healthIdDao.getHealthIdDetails(customerId, ((Short) selfMap.get("id")).intValue());
			if (ObjectUtils.isNotEmpty(healthId) && ObjectUtils.isNotEmpty(healthId.getGender())) {
				if (healthId.getGender().equalsIgnoreCase("M")) {
					relationsList = relationsList
							.stream()
							.filter(e -> (!(((String) e.get("name")).equalsIgnoreCase("Spouse") ||
									((String) e.get("name")).equalsIgnoreCase("Husband"))))
							.collect(Collectors.toList());
				} else {
					relationsList = relationsList
							.stream()
							.filter(e -> (!((((String) e.get("name")).equalsIgnoreCase("Spouse") ||
									((String) e.get("name")).equalsIgnoreCase("Wife")))))
							.collect(Collectors.toList());
				}
			} else {
				relationsList = relationsList
						.stream()
						.filter(e -> (!((((String) e.get("name")).equalsIgnoreCase("Wife") ||
								((String) e.get("name")).equalsIgnoreCase("Husband")))))
						.collect(Collectors.toList());
			}
		}
		return relationsList;
	}
	
}
