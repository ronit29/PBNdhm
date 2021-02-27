package com.pb.dp.model;

import java.util.Date;

public class SearchDocFilter {
	
	private String docOwner;
	private String docName;
	private String tags;
	private Date updatedFrom;
	private Date updatedAt;
	
	
	public String getDocOwner() {
		return docOwner;
	}
	public void setDocOwner(String docOwner) {
		this.docOwner = docOwner;
	}
	
	public String getDocName() {
		return docName;
	}
	public void setDocName(String docName) {
		this.docName = docName;
	}

	public String getTags() {
		return tags;
	}
	public void setTags(String tags) {
		this.tags = tags;
	}
	public Date getUpdatedFrom() {
		return updatedFrom;
	}
	public void setUpdatedFrom(Date updatedFrom) {
		this.updatedFrom = updatedFrom;
	}
	public Date getUpdatedAt() {
		return updatedAt;
	}
	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}
	
}
