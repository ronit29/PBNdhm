package com.pb.dp.enums;

public enum NdhmVerifyOperation {

    REGISTER(1,"REGISTER"),
    GET_PROFILE(2,"GET_PROFILE"),
    UPDATE_PROFILE(3,"UPDATE_PROFILE"),
    DELETE_PROFILE(4,"DELETE_PROFILE");

    private final int operationId;
    private final String operationName;

    NdhmVerifyOperation(int operationId, String operationName) {
        this.operationId = operationId;
        this.operationName = operationName;
    }

    public int getOperationId() {
        return operationId;
    }

    public String getOperationName() {
        return operationName;
    }
}
