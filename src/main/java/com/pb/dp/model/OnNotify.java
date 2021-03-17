package com.pb.dp.model;

public class OnNotify {
	
	private String requestId;

	private String timestamp;

	private Resp resp;

	private Acknowledgement acknowledgement;

	private Error error;

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

	public void setResp(Resp resp) {
		this.resp = resp;
	}

	public Resp getResp() {
		return this.resp;
	}

	public void setAcknowledgement(Acknowledgement acknowledgement) {
		this.acknowledgement = acknowledgement;
	}

	public Acknowledgement getAcknowledgement() {
		return this.acknowledgement;
	}

	public void setError(Error error) {
		this.error = error;
	}

	public Error getError() {
		return this.error;
	}
}
