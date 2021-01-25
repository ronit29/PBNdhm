package com.policybazaar.docprimNdhm.login.service;

public interface LoginQuery {

    String INSERT_OTP_DETAILS = "INSERT INTO DocprimeNDHM.dbo.sms_otp_details (CountryCode, MobileNo, OTP, Message, Response, SMSType, UUID) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?)";
}
