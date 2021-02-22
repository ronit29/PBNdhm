package com.pb.dp.enums;

public enum ResponseStatus {

	
	SUCCESS(1,"SUCCESS"),
	FAILURE(2,"FAILURE"),
	MAN_VALIDATION(3,"Mandatory Fields Missing"),
	CACHE_FAILURE(4,"Unable to load Auth Details"), 
	CLIENT_KEY_NOT_PRESENT(5,"Client Key not present"), 
	INVALID_CLIENT_KEY(6,"Invalid client key"),
	CUSTOMER_ID_NOT_PRESENT(7,"CustomerId is missing"),
	IS_PRIMARY_VALIDATION(8,"Cannot update isActive false for primary detail"),
	TYPE_MISMATCH(9,"Data type mismatch for :"),
	LENGTH_EXCEEDED(10,"Length of String exceeded for :"),
	INVALID_DATE_FORMAT(11,"Invalid date format for :"),
	INVALID_REQUEST(12,"Invalid Request"),
	NO_RECORD_FOUND(13,"No record found"), 
	INVALID_CHECKSUM(14,"Invalid checksum"),
	INVALID_FORMAT_PARAM(15,"Invalid Format"),
	INVALID_AUTH_KEY(16,"Auth Key Invalid for CLient Key"),
	EMPTY_PARAMETER(17,"Empty Parameter"),
	DUPLICATE_RECORD(18,"Duplicate Record Found"),
	INVALID_CUSTOMER_ID(19,"CustomerId Invalid for CLient Key"),
	INVALID_INPUT(20,"Invalid input"),
	INVALID_XTOKEN(21,"Invalid xToken"), 
	AUTH_INIT_FAILED(22,"Auth Init Api Failed for invalid xToken"),
	INVALID_SESSION(23,"Invalid Session Id"),
	NDHM_FAILURE(24,"NDHM API Failed"),
	NDHM_HEALTHID_EXIST(25,"Health Id not available"),
	NDHM_USER_EXIST(25,"Health Id for user exist on provided mobile no");

	private final int statusId;
	
	private final String statusMsg;

	private ResponseStatus(int statusId, String statusMsg ) {
		this.statusId = statusId;
		this.statusMsg = statusMsg;
	}

	public int getStatusId() {
		return statusId;
	}
	public String getStatusMsg() {
		return statusMsg;
	}
	public static ResponseStatus valueOf(int statusId) {
		for (ResponseStatus responseStatus : values()) {
			if (responseStatus.statusId == statusId) {
				return responseStatus;
			}
		}
		return null;
	}

}
