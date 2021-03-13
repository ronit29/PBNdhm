package com.pb.dp.model;

public class Query {
	
	private String id;

	private String purpose;

	private String authMode;

	private Requester requester;

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return this.id;
	}

	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}

	public String getPurpose() {
		return this.purpose;
	}

	public void setAuthMode(String authMode) {
		this.authMode = authMode;
	}

	public String getAuthMode() {
		return this.authMode;
	}

	public void setRequester(Requester requester) {
		this.requester = requester;
	}

	public Requester getRequester() {
		return this.requester;
	}
}