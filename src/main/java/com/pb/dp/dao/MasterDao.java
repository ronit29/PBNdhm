package com.pb.dp.dao;

import java.util.List;
import java.util.Map;

/**
 * This interface defines dao layer for Master
 * 
 * @author Aditya Rathore
 *
 */
public interface MasterDao {

	/**
	 * This method is used to get list of states
	 * 
	 * @return
	 * @throws Exception
	 */
	List<Map<String,Object>> getState() throws Exception;
	
	/**
	 * This method is used to get Districts based on stateCode
	 * 
	 * @param stateCode
	 * @return
	 * @throws Exception
	 */
	List<Map<String,Object>> getDistrictsForState(Integer stateCode) throws Exception;
	
	/**
	 * This method is used to get list of relations
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> getRelations() throws Exception ;

	List<Map<String, Object>> getDocTypes() throws Exception;
}
