package com.pb.dp.service;

import java.util.List;
import java.util.Map;

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
	
	@Override
	public List<Map<String,Object>> getState() throws Exception{
		return masterDao.getState();
	}

	@Override
	public List<Map<String, Object>> getDistrictsForState(Integer stateCode) throws Exception {
		return masterDao.getDistrictsForState(stateCode);
	}
	
}
