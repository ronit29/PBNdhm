package com.pb.dp.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class LoggerUtil {
    private static final Logger logger = LoggerFactory.getLogger(LoggerUtil.class);
     public void logApiData(String url, String jsonPayload, Map<String, String> headers, Map<String, Object> response){
        logger.info("url : {},"+'\n'+" headers : {},"+'\n'+" jsonPayload :{},"+'\n'+" api Response : {} ",url,headers,jsonPayload,response);
    }
}
