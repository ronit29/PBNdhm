package com.pb.dp.healthIdCreation.model;

public class CustomerDetails {

    private long mobileNo;
    private String firstName;
    private String midName;
    private String lastName;
    private String fullName;
    private String gender;
    private String dob;
    private String relationship;
    private Integer relationId;
    private String healthId;
    private String healthIdNo;
    private String address;
    private Long state;
    private String stateName;
    private Long district;
    private String districtName;
    private String emailId;
    private Boolean isKyc;
    private Integer pincode;
    private String token;
    private Long dbId;
    private Long addressId;
    private String txnId;



    public long getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(long mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    public String getHealthId() {
        return healthId;
    }

    public void setHealthId(String healthId) {
        this.healthId = healthId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Long getState() {
        return state;
    }

    public void setState(Long state) {
        this.state = state;
    }

    public Long getDistrict() {
        return district;
    }

    public void setDistrict(Long district) {
        this.district = district;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getMidName() {
        return midName;
    }

    public void setMidName(String midName) {
        this.midName = midName;
    }

    public String getHealthIdNo() {
        return healthIdNo;
    }

    public void setHealthIdNo(String healthIdNo) {
        this.healthIdNo = healthIdNo;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public String getDistrictName() {
        return districtName;
    }

    public void setDistrictName(String districtName) {
        this.districtName = districtName;
    }

    public Boolean getKyc() {
        return isKyc;
    }

    public void setKyc(Boolean kyc) {
        isKyc = kyc;
    }

    public Integer getPincode() {
        return pincode;
    }

    public void setPincode(Integer pincode) {
        this.pincode = pincode;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Integer getRelationId() {
        return relationId;
    }

    public void setRelationId(Integer relationId) {
        this.relationId = relationId;
    }

    public Long getDbId() {
        return dbId;
    }

    public void setDbId(Long dbId) {
        this.dbId = dbId;
    }

    public Long getAddressId() {
        return addressId;
    }

    public void setAddressId(Long addressId) {
        this.addressId = addressId;
    }

    public String getTxnId() {
        return txnId;
    }

    public void setTxnId(String txnId) {
        this.txnId = txnId;
    }

    @Override
    public String toString() {
        return "CustomerDetails{" +
                "mobileNo=" + mobileNo +
                ", firstName='" + firstName + '\'' +
                ", midName='" + midName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", fullName='" + fullName + '\'' +
                ", gender='" + gender + '\'' +
                ", dob='" + dob + '\'' +
                ", relationship='" + relationship + '\'' +
                ", relationId=" + relationId +
                ", healthId='" + healthId + '\'' +
                ", healthIdNo='" + healthIdNo + '\'' +
                ", address='" + address + '\'' +
                ", state=" + state +
                ", stateName='" + stateName + '\'' +
                ", district=" + district +
                ", districtName='" + districtName + '\'' +
                ", emailId='" + emailId + '\'' +
                ", isKyc=" + isKyc +
                ", pincode=" + pincode +
                ", token='" + token + '\'' +
                ", dbId=" + dbId +
                ", addressId=" + addressId +
                ", txnId=" + txnId +
                '}';
    }
}

