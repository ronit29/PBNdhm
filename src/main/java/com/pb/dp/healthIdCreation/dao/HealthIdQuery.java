package com.pb.dp.healthIdCreation.dao;

public interface HealthIdQuery {

    String ADD_CUSTOMER = "INSERT INTO DocprimeNDHM.dbo.customer (firstName, lastName, dob, relationship, address_id,"
            + " emailId, mobile, gender, isActive, createdAt, createdBy) "
            + " VALUES (:firstName, :lastName, :dob, :relation, :address, :email, :mobile, :gender, 1, GETDATE(), 1)";

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

    String GET_CUSTOMER = "SELECT * from DocprimeNDHM.dbo.customer where id = :id and mobile = :mobile";

    String GET_ADDRESS = "SELECT * from DocprimeNDHM.dbo.address where id = :id";

    String UPDATE_NDHM_TXN_ID = "UPDATE DocprimeNDHM.dbo.customer SET txnId = :txnId WHERE id = :custId";

    String UPDATE_NDHM_MOBILE_TOKEN = "UPDATE DocprimeNDHM.dbo.customer SET token = :token WHERE id = :custId and mobile = :mobile";

    String UPDATE_CUSTOMER_DETAILS = "UPDATE DocprimeNDHM.dbo.customer SET firstName = :firstName, lastName = :lastName, " +
            "dob = :dob, relationship = :relation, address_id = :address, emailId = :email, gender = :gender WHERE id = :custId and mobile " +
            "= :mobile and isActive = 1";


}
