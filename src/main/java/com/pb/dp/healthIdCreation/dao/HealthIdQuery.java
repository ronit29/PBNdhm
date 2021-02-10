package com.pb.dp.healthIdCreation.dao;

public interface HealthIdQuery {

    String ADD_CUSTOMER = "INSERT INTO DocprimeNDHM.dbo.customer (mobile, otp, otpCreatedAt, isActive, createdAt, createdBy) "
            + " VALUES (:mobile, :otp, GETDATE(), 1, GETDATE(), -1)";

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

    String UPDATE_PROFILE_TXN_ID = "UPDATE DocprimeNDHM.dbo.healthId SET txnId = :txnId WHERE customerId = :custId and healthId = :healthId";

    String UPDATE_NDHM_MOBILE_TOKEN = "UPDATE DocprimeNDHM.dbo.customer SET token = :token WHERE id = :custId and mobile = :mobile";

    String UPDATE_CUSTOMER_DETAILS = "UPDATE DocprimeNDHM.dbo.customer SET firstName = :firstName, lastName = :lastName, " +
            "dob = :dob, relationship = :relation, address_id = :address, emailId = :email, gender = :gender, healthId = :healthId, " +
            "updatedBy = -1, updatedAt = GETDATE() WHERE id = :custId and mobile = :mobile and isActive = 1";

    String UPDATE_PROFILE_CUSTOMER = "UPDATE DocprimeNDHM.dbo.healthId SET firstName = :firstName, lastName = :lastName, " +
            "dob = :dob, relation = :relation, email = :email, gender = :gender, " +
            "updatedBy = -1, updatedAt = GETDATE() WHERE customerId = :custId and healthId = :healthId and isActive = 1";

    String UPDATE_PROFILE_ADDRESS = "UPDATE DocprimeNDHM.dbo.address SET line1 = :address, districtId = :districtId, " +
            "stateId = :stateId, pincode = :pincode, updatedBy = -1, updatedAt = GETDATE()  WHERE id = (select h.addressId from healthId h where h.customerId = :custId and healthId = :healthId) ";

    String ADD_HEALTH_ID = "INSERT INTO DocprimeNDHM.dbo.healthId (healthId, healtIdNo, customerId, healthIdToken, isKyc, isActive, createdAt, createdBy) "
            + " VALUES (:healthId, :healtIdNo, :custId, :token, 0, 1, GETDATE(), -1)";

    String GET_CUSTOMER_BY_MOBILE = "SELECT * from DocprimeNDHM.dbo.customer where mobile = :mobile";

    String UPDATE_CUSTOMER = "UPDATE DocprimeNDHM.dbo.customer SET otp = :otp, otpCreatedAt = GETDATE(), " +
            "updatedBy = -1, updatedAt = GETDATE() WHERE id = :id and mobile = :mobile and isActive = 1";

    String ADD_HEALTH_ID_DEMOGRAPHIC = "INSERT INTO DocprimeNDHM.dbo.healthId (healthId, customerId, isActive, createdAt, createdBy, relation," +
            " firstName, lastName, addressId, email, dob, gender) VALUES (:healthId, :custId, 0, GETDATE(), -1, :relation, :firstName," +
            " :lastName, :address, :email, :dob, :gender)";

    String UPDATE_HEALTH_ID_TXN_ID = "UPDATE DocprimeNDHM.dbo.healthId SET txnId = :txnId WHERE id = :id";

    String UPDATE_HEALTH_ID_MOB_TOKEN = "UPDATE DocprimeNDHM.dbo.healthId SET token = :token WHERE customerId = :custId and txnId = :txnId";

    String GET_HEALTH_ID_By_TXN_AND_CUST_ID = "SELECT * from DocprimeNDHM.dbo.healthId where customerId = :custId and txnId = :txnId";

    String GET_HEALTH_ID_BY_RELATION_AND_CUST_ID = "SELECT * from DocprimeNDHM.dbo.healthId where customerId = :custId and relation = :relation and isActive = 1";

    String UPDATE_HEALTHID_DATA = "UPDATE DocprimeNDHM.dbo.healthId SET healthId = :healthId, healtIdNo = :healtIdNo, healthIdToken = :token," +
            " isKyc = 0, isActive = 1, updatedBy = -1, updatedAt = GETDATE()  WHERE customerId = :custId and txnId = :txnId";

    String GET_BY_HEALTH_ID = "SELECT * from DocprimeNDHM.dbo.healthId WHERE healthId = :healthId and isActive = 1";

    String DELETE_ADDRESS_BY_TXN_AND_CUST_ID = "DELETE FROM DocprimeNDHM.dbo.address WHERE id = (SELECT addressId from DocprimeNDHM.dbo.healthId where customerId = :custId and txnId = :txnId)";

    String DELETE_HEALTHID_BY_TXN_AND_CUST_ID = "DELETE FROM DocprimeNDHM.dbo.healthId WHERE customerId = :custId and txnId = :txnId";
}
