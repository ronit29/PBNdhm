package com.pb.dp.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.pb.dp.exception.CipherException;
import com.pb.dp.model.OAuthUrlInfo;
import com.pb.dp.model.PropertyConfigModel;
import com.pb.dp.service.ConfigService;
import com.pb.dp.util.EncryptionUtil;
import com.pb.dp.util.IpUtils;
import com.pb.dp.util.ValidationUtil;
import com.pb.dp.util.ValidationUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component(value = "OAuthSecurityFilter")
public class OAuthSecurityFilter implements Filter {

    public static final String CUSTOMER_ID_CODE = "drmtsc";
    public static final String APPLICATION_ID_CODE = "dntclp";
    public static final String MOBILE_ID_CODE = "lbm";
    private static final String DEFAULT_SECRET_CODE = CUSTOMER_ID_CODE;
    private static final String DEFAULT_TOKEN_TYPE = "bearer";
    private static final List<String> POSSIBLE_CLIENT_CODES = new ArrayList<>();

    private final Logger logger = LoggerFactory.getLogger(OAuthSecurityFilter.class);

    private RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private ConfigService configService;

    @Autowired
    private ValidationUtil validationUtil;

    @Value("#{'${api.threashold.response.time:2}'}")
    private Integer apiThreasholdResponseTime;

    @Value("#{'${internal.domain.list:cs.policybazaar.com}'.split(',')}")
    private List<String> INTERNAL_DOMAINS;

    @Value("${oauthserver.base.url}")
    private String oauthServerBaseUrl;

    public OAuthSecurityFilter() {
        POSSIBLE_CLIENT_CODES.add(CUSTOMER_ID_CODE);
        POSSIBLE_CLIENT_CODES.add(APPLICATION_ID_CODE);
        POSSIBLE_CLIENT_CODES.add(MOBILE_ID_CODE);
    }

    private boolean isInternalDomain(String serverName) {
        if (serverName != null) {
            for (String secureDomain : INTERNAL_DOMAINS) {
                if (serverName.contains(secureDomain)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) res;
        HttpServletRequest request = (HttpServletRequest) req;
        JsonObject obj = new JsonObject();
        boolean validRequest = true;

        if (!isInternalDomain(request.getServerName())) {
            try {
                String uri = request.getRequestURI();
                String[] uriComponents = uri.split("/cs");
                String methodMapping = "";
                if (uriComponents.length > 1) {
                    methodMapping = uriComponents[1];
                    if (methodMapping.contains("?")) {
                        methodMapping = methodMapping.split("[?]")[0];
                    }
                }
                OAuthUrlInfo urlInfo = configService.getServiceUrl(methodMapping);
                if (urlInfo == null) {
                    validRequest = false;
                    response.sendError(401);
                } else if (request.getMethod().equalsIgnoreCase("HEAD")) {
                    validRequest = false;
                    response.setStatus(200);
                } else {
                    if (request.getMethod().equalsIgnoreCase("GET") && null != urlInfo.getRequiredParameters()) {
                        for (String param : urlInfo.getRequiredParameters()) {
                            if (request.getParameter(param) == null) {
                                response.setStatus(200);
                                response.getWriter().write("Missing mandatory parameter : " + param);
                                validRequest = false;
                                break;
                            }
                        }
                    }
                    if (urlInfo.isOauthRequired()) {
                        String clientKey = request.getHeader("clientKey");
                        String authKey = request.getHeader("authKey");
                        Map<String, Object> validationResponse = validationUtil.validateClientAndAuthKey(clientKey, authKey);
                        if (validationResponse != null) {
                            logger.error("Invalid ClientKey and AuthKey validation for clientKey :{}, authKey: {}, IP :{}, URI: {}", new Object[]{clientKey, authKey, IpUtils.getIpFromRequest(request), uri});
                            PropertyConfigModel propertyConfigModel = configService.getPropertyConfig("enableClientAuthSecurity");
                            if (propertyConfigModel != null && propertyConfigModel.getValue().equals("true")) {
                                response.setStatus(200);
                                response.setContentType("application/json");
                                response.setCharacterEncoding("UTF-8");
                                response.getWriter().write(new ObjectMapper().writeValueAsString(validationResponse));
                                validRequest = false;
                            }
                        }
                    }

                    if (validRequest && urlInfo.isSecuredUrl()) {
                        String authInfo = request.getHeader("Authorization");
                        if (StringUtils.isEmpty(authInfo) || authInfo.split(" ").length < 2) {
                            validRequest = false;
                            obj.addProperty("ok", 0);
                            obj.addProperty("msg", "Missing 'Authorization' value/s in header");
                            response.getWriter().print(obj);
                        } else {
                            String[] authComponents = authInfo.split(" ");
                            String accessToken = authComponents[0];
                            String secretKeyEncoded = authComponents[1];
                            String secretKey = null;
                            secretKey = EncryptionUtil.decryptAES128Filter(secretKeyEncoded);

                            logger.debug("Resource server :: Encoded secret key received in filter is [ " + secretKeyEncoded + " ]");
                            logger.debug("Resource server :: Decoded secret key is [ " + secretKey + " ]");

                            String secretCode = DEFAULT_SECRET_CODE;
                            String token_type = DEFAULT_TOKEN_TYPE;
                            if (authComponents.length > 2) {
                                // secretCode's possible values are ['drmtsc' if client is customer id, 'dntclp' if application id
                                // and
                                // 'lbm' if mobile number ]
                                secretCode = authComponents[2];
                            }
                            if (authComponents.length > 3) {
                                token_type = authComponents[3];
                            }
                            if (StringUtils.isEmpty(accessToken) || StringUtils.isEmpty(secretKey) || !POSSIBLE_CLIENT_CODES.contains(secretCode)
                                    || !ValidationUtils.isValidOAuthKey(secretKey, secretCode)) {
                                validRequest = false;
                                obj.addProperty("ok", 0);
                                // code possible values (drmtsc, dntclp, lbm)
                                obj.addProperty("msg", "Missing 'Authorization' value/s or invalid secret key in header");
                                response.getWriter().print(obj);
                            } else {
                                String validateTokenUrl = oauthServerBaseUrl + "/token/validate?access_token=" + accessToken
                                        + "&secretKey=" + secretKeyEncoded + "&code=" + secretCode;
                                try {
                                    long startTime = System.currentTimeMillis();
                                    Map<String, Object> apiResponse = restTemplate.getForObject(validateTokenUrl, HashMap.class, Map.class);
                                    long endTime = System.currentTimeMillis();
                                    int processingTime = (int) ((endTime - startTime) / 1000);
                                    if (processingTime >= apiThreasholdResponseTime) {
                                        logger.warn("processing time exceded for : {} taking : {}",validateTokenUrl, processingTime);
                                    }
                                    if (apiResponse == null || apiResponse.get("ok") == null || !apiResponse.get("ok").toString().equals("1")) {
                                        validRequest = false;
                                        obj.addProperty("ok", 0);
                                        obj.addProperty("msg", "Invalid token");
                                        response.getWriter().print(obj);
                                    }
                                } catch (HttpClientErrorException e) {
                                    validRequest = false;
                                    logger.error("Error occurred while trying to validate access token ::: " + e.getMessage(), e);
                                    response.sendError(404, "OAuth server is not responding or may be down. Please contact administrator.");
                                }
                            }
                        }
                    }

                }
            } catch (CipherException e1) {
                validRequest = false;
                obj.addProperty("ok", 0);
                obj.addProperty("msg", e1.getMessage());
                response.getWriter().print(obj);
            } catch (Exception ignored) {

            }
        }
        if (validRequest) {
            chain.doFilter(req, res);
        }
    }

    public void init(FilterConfig filterConfig) {

    }

    public void destroy() {
    }

}