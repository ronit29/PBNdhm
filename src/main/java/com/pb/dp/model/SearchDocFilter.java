package com.pb.dp.model;

import java.util.Date;

public class SearchDocFilter {
	
	private Integer docId;
	private String docName;
	private String tags;
	private Date updatedFrom;
	private Date updatedTo;
	private Integer docType;
	private String medicEntityName;
	
	
	public Integer getDocId() {
		return docId;
	}
	public void setDocId(Integer docId) {
		this.docId = docId;
	}
	
	public Integer getDocType() {
		return docType;
	}
	public void setDocType(Integer docType) {
		this.docType = docType;
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

	public Date getUpdatedTo() {
		return updatedTo;
	}
	public void setUpdatedTo(Date updatedTo) {
		this.updatedTo = updatedTo;
	}
	public String getMedicEntityName() {
		return medicEntityName;
	}
	public void setMedicEntityName(String medicEntityName) {
		this.medicEntityName = medicEntityName;
	}
	
}
