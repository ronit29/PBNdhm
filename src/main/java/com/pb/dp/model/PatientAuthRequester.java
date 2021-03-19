package com.pb.dp.model;

public class PatientAuthRequester {
    //Enum:[ HIP, HIU, HEALTH_LOCKER ]
    private String type;
    private String id;

    public PatientAuthRequester() {
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public PatientAuthRequester(String type, String id) {
        this.type = type;
        this.id = id;
    }

    @Override
    public String toString() {
        return "PatientAuthRequester{" +
                "type='" + type + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}
