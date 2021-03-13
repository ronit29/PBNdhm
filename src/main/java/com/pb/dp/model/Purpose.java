package com.pb.dp.model;

public class Purpose {
	private String text;

	private String code;

	private String refUri;

	public void setText(String text) {
		this.text = text;
	}

	public String getText() {
		return this.text;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getCode() {
		return this.code;
	}

	public void setRefUri(String refUri) {
		this.refUri = refUri;
	}

	public String getRefUri() {
		return this.refUri;
	}
}