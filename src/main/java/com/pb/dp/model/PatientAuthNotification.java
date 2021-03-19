package com.pb.dp.model;

import java.util.Date;

public class PatientAuthNotification {

    private String requestId;
    //Date time format in UTC, includes miliseconds YYYY-MM-DDThh:mm:ss.vZ
    private String timestamp;

    private PatientAuth auth;

    public PatientAuthNotification() {
    }

    public PatientAuthNotification(String requestId, String timestamp, PatientAuth auth) {
        this.requestId = requestId;
        this.timestamp = timestamp;
        this.auth = auth;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public PatientAuth getAuth() {
        return auth;
    }

    public void setAuth(PatientAuth auth) {
        this.auth = auth;
    }

    @Override
    public String toString() {
        return "PatientAuthNotification{" +
                "requestId='" + requestId + '\'' +
                ", timestamp=" + timestamp +
                ", auth=" + auth +
                '}';
    }
}
