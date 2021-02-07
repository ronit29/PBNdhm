package com.pb.dp.healthIdCreation.dao;

public interface HealthIdQuery {

    String ADD_CUSTOMER = "INSERT INTO DocprimeNDHM.dbo.customer (firstName, mobile, otp, otpCreatedAt, isActive, createdAt, createdBy) "
            + " VALUES (:firstName, :mobile, :otp, GETDATE(), 1, GETDATE(), -1)";

    String ADD_CUSTOMER1 = "INSERT INTO DocprimeNDHM.dbo.customer (firstName, lastName, dob, relationship, address_id,"
            + " emailId, mobile, gender, isActive, createdAt, createdBy) "
            + " VALUES (:firstName, :lastName, :dob, :relation, (SELECT id FROM DocprimeNDHM.dbo.address WHERE"
            + " type='main'), :email, :mobile, :gender, 1, GETDATE(), 1)";

    String ADD_CUSTOMER_ADDRESS = "INSERT INTO DocprimeNDHM.dbo.address (line1, districtId, stateId, "
            + " createdAt, createdBy) "
            + " VALUES (:line1, :districtId, :stateId, GETDATE(), 1)";

    String ADD_NDHM_OTP_TXNID = "INSERT INTO DocprimeNDHM.dbo.ndhm_otp (mobile, txnId, otpType, createdAt, createdBy) "
            + " VALUES (:mobile, :txnId, 1, GETDATE(), 1)";

    String UPDATE_NDHM_OTP = "UPDATE DocprimeNDHM.dbo.ndhm_otp SET otp = :otp WHERE mobile = :mobile and "
            + " txnId = :txnId";

    String UPDATE_NDHM_OTP_TOKEN = "UPDATE DocprimeNDHM.dbo.ndhm_otp SET token = :token WHERE mobile = :mobile and "
            + " txnId = :txnId";

    String GET_CUSTOMER = "SELECT * from DocprimeNDHM.dbo.customer where id = :id";

    String GET_ADDRESS = "SELECT * from DocprimeNDHM.dbo.address where id = :id";

    String UPDATE_NDHM_TXN_ID = "UPDATE DocprimeNDHM.dbo.customer SET txnId = :txnId WHERE id = :custId";

    String UPDATE_NDHM_MOBILE_TOKEN = "UPDATE DocprimeNDHM.dbo.customer SET token = :token WHERE id = :custId and mobile = :mobile";

    String UPDATE_CUSTOMER_DETAILS = "UPDATE DocprimeNDHM.dbo.customer SET firstName = :firstName, lastName = :lastName, " +
            "dob = :dob, relationship = :relation, address_id = :address, emailId = :email, gender = :gender, healthId = :healthId, " +
            "updatedBy = -1, updatedAt = GETDATE() WHERE id = :custId and mobile = :mobile and isActive = 1";

    String UPDATE_PROFILE_CUSTOMER = "UPDATE DocprimeNDHM.dbo.customer SET firstName = :firstName, lastName = :lastName, " +
            "dob = :dob, relationship = :relation, emailId = :email, gender = :gender, " +
            "updatedBy = -1, updatedAt = GETDATE() WHERE id = :custId and isActive = 1";

    String UPDATE_PROFILE_ADDRESS = "UPDATE DocprimeNDHM.dbo.address SET line1 = :address, districtId = :districtId, " +
            "stateId = :stateId, pincode = :pincode, updatedBy = -1, updatedAt = GETDATE()  WHERE id = (select c.address_id from customer c where c.id = :custId) ";

    String ADD_HEALTH_ID = "INSERT INTO DocprimeNDHM.dbo.healthId (healthId, healtIdNo, customerId, healthIdToken, isKyc, isActive, createdAt, createdBy) "
            + " VALUES (:healthId, :healtIdNo, :custId, :token, 0, 1, GETDATE(), -1)";
}
