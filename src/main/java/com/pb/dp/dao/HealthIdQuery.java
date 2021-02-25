package com.pb.dp.dao;

public interface HealthIdQuery {

    String ADD_CUSTOMER = "INSERT INTO DocprimeNDHM.dbo.customer (firstName,mobile, otp, otpCreatedAt, isActive, createdAt, createdBy) "
            + " VALUES (:firstName,:mobile, :otp, GETDATE(), 1, GETDATE(), -1)";

    String ADD_CUSTOMER_ADDRESS = "INSERT INTO DocprimeNDHM.dbo.address (line1, districtId, stateId, "
            + " createdAt, createdBy) "
            + " VALUES (:line1, :districtId, :stateId, GETDATE(), 1)";

    String UPDATE_PROFILE_CUSTOMER = "UPDATE DocprimeNDHM.dbo.healthId SET firstName = :firstName, lastName = :lastName, " +
            "dob = :dob, relation = :relation, email = :email, gender = :gender, profilePhoto = :profilePhoto, " +
            "updatedBy = -1, updatedAt = GETDATE() WHERE customerId = :custId and healthId = :healthId and isActive = 1";

    String UPDATE_PROFILE_ADDRESS = "UPDATE DocprimeNDHM.dbo.address SET line1 = :address, districtId = :districtId, " +
            "stateId = :stateId, pincode = :pincode, updatedBy = -1, updatedAt = GETDATE()  WHERE id = (select h.addressId from healthId h where h.customerId = :custId and healthId = :healthId) ";

    String GET_CUSTOMER_BY_MOBILE = "SELECT * from DocprimeNDHM.dbo.customer where mobile = :mobile";

    String UPDATE_CUSTOMER = "UPDATE DocprimeNDHM.dbo.customer SET otp = :otp, otpCreatedAt = GETDATE(), " +
            "updatedBy = -1, updatedAt = GETDATE() WHERE id = :id and mobile = :mobile and isActive = 1";

    String ADD_HEALTH_ID_DEMOGRAPHIC = "INSERT INTO DocprimeNDHM.dbo.healthId (healthId, healtIdNo, customerId, isActive, createdAt, createdBy, relation," +
            " firstName, lastName, addressId, email, dob, gender, healthIdToken) VALUES (:healthId, :healtIdNo, :custId, 1, GETDATE(), -1, :relation, :firstName," +
            " :lastName, :address, :email, :dob, :gender, :token)";

    String GET_HEALTH_ID_BY_RELATION_AND_CUST_ID = "SELECT * from DocprimeNDHM.dbo.healthId where customerId = :custId and relation = :relation and isActive = 1";

    String GET_BY_HEALTH_ID = "SELECT * from DocprimeNDHM.dbo.healthId WHERE healthId = :healthId and isActive = 1";
}
