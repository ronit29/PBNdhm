package com.pb.dp.model;

public class Authorise {
	
	private String requestId;

	private String timestamp;

	private Query query;

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public String getRequestId() {
		return this.requestId;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getTimestamp() {
		return this.timestamp;
	}

	public void setQuery(Query query) {
		this.query = query;
	}

	public Query getQuery() {
		return this.query;
	}
}