package com.pb.dp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class PatientAuth {

    //    description:
//    depending on the purpose of auth, as specified in /auth/init, the response may include the following
//    LINK - only returns accessToken
//    KYC - only returns patient
//    KYC_AND_LINK - returns both accessToken and patient
    private String transactionId;
    //Enum:[ GRANTED, DENIED ]
    private String status;
    @JsonIgnore
    private String accessToken;
    private AccessTokenValidity validity;
    private PatientDemographicResponse patient;

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public AccessTokenValidity getValidity() {
        return validity;
    }

    public void setValidity(AccessTokenValidity validity) {
        this.validity = validity;
    }

    public PatientDemographicResponse getPatient() {
        return patient;
    }

    public void setPatient(PatientDemographicResponse patient) {
        this.patient = patient;
    }

    @Override
    public String toString() {
        return "PatientAuth{" +
                "transactionId='" + transactionId + '\'' +
                ", status='" + status + '\'' +
                ", accessToken='" + accessToken + '\'' +
                ", validity=" + validity +
                ", patient=" + patient +
                '}';
    }
}
