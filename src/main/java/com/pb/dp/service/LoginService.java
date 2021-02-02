package com.pb.dp.service;

import java.util.Map;

public interface LoginService {
    Map<String, Object> sendOtp(Long mobile) throws Exception;
}
