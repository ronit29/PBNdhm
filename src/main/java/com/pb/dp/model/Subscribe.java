package com.pb.dp.model;

public class Subscribe {

	private String requestId;

	private String timestamp;

	private Subscription subscription;

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

	public void setSubscription(Subscription subscription) {
		this.subscription = subscription;
	}

	public Subscription getSubscription() {
		return this.subscription;
	}

}
