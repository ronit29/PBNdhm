package com.policybazaar.docprimNdhm.master.service;

import java.util.List;
import java.util.Map;

/**
 * This interface defines service layer for master
 * 
 * @author Aditya Rathore
 *
 */
public interface MasterService {

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
}
