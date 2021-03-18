package com.pb.dp.model;

import java.util.Date;

public class AccessTokenValidity {
        // Enum:[ LINK, KYC, KYC_AND_LINK ]
        private String purpose;
        private PatientAuthRequester requester;
        //Date time format in UTC, includes miliseconds YYYY-MM-DDThh:mm:ss.vZ
        private Date expiry;
        private Integer limit;

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public PatientAuthRequester getRequester() {
        return requester;
    }

    public void setRequester(PatientAuthRequester requester) {
        this.requester = requester;
    }

    public Date getExpiry() {
        return expiry;
    }

    public void setExpiry(Date expiry) {
        this.expiry = expiry;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    @Override
    public String toString() {
        return "AccessTokenValidity{" +
                "purpose='" + purpose + '\'' +
                ", requester=" + requester +
                ", expiry=" + expiry +
                ", limit=" + limit +
                '}';
    }

}

