package com.pb.dp.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.pb.dp.exception.CipherException;
import com.pb.dp.model.AuthDetail;
import com.pb.dp.service.ConfigService;

public class CheckSumUtil {

	public static final String SIGNATURE = "signature";
	@Autowired
	private ConfigService configService;
    private final static Logger logger = LoggerFactory.getLogger(CheckSumUtil.class);

    public String generateCheckSumWithMapping(BasicDBObject document) {
        Map<String, Object> requestMap = new HashMap<>(document);
        StringBuilder key = new StringBuilder();
        StringBuilder keyItems = new StringBuilder();
        LinkedList<Map<String, Object>> subList = new LinkedList<>();
        if (requestMap.size() > 0 && requestMap.get("clientKey") != null) {
            String clientKey = requestMap.get("clientKey").toString();
            AuthDetail authDetail = configService.getAuthDetail(clientKey);
            if (authDetail != null) {
                String accessKey = authDetail.getHash_access_key();
                String secretKey = authDetail.getHash_secret_key();
                try {
                    BasicDBList list = (BasicDBList) requestMap.get("mapping");
                    if (null != list && list.size() > 0) {
                        list.sort((o1, o2) -> {
                            BasicDBObject bo1 = (BasicDBObject) o1;
                            BasicDBObject bo2 = (BasicDBObject) o2;
                            return Integer.compare(bo1.getInt("idx"), bo2.getInt("idx"));
                        });
                        for (Object itemsMap : list)
                            subList.add(new TreeMap<>((Map<String, Object>) itemsMap));
                        if (subList.size() > 0) {
                            keyItems.append("[");
                            for (Map<String, Object> subMap : subList) {
                                keyItems.append("{");
                                for (Map.Entry<String, Object> entry : subMap.entrySet()) {
                                    keyItems.append(entry.getKey()).append("=").append(entry.getValue()).append(";");
                                }
                                keyItems.append("};");
                            }
                            keyItems = new StringBuilder(keyItems.substring(0, keyItems.length() - 1));
                            keyItems.append("]");
                        }
                        requestMap.put("mapping", keyItems);
                    }

                    while (requestMap.values().remove(null)) ;

                    requestMap.remove("signature");
                    Map<String, Object> map = new TreeMap<>(requestMap);
                    key.append(accessKey).append("|");
                    for (Map.Entry<String, Object> entry : map.entrySet())
                        key.append(entry.getKey()).append("=").append(entry.getValue()).append(";");
                    key.append("|").append(secretKey);
                    logger.info("checksum String :{}",key.toString());
                    return EncryptionUtil.decryptSHA256Filter(key.toString());
                } catch (CipherException e1) {
                    e1.printStackTrace();
                    return null;
                }
            }
        }
        return null;
    }
    
    

//    public static String generateCheckSum(Map<String, Object> requestMap) {
//        LinkedList<Map<String, Object>> subList = new LinkedList<>();
//        if (requestMap.size() > 0 && requestMap.get("clientKey") != null) {
//            String clientKey = requestMap.get("clientKey").toString();
//            AuthDetail authDetail = ConfigServiceImpl.getAuthDetail(clientKey);
//            if (authDetail != null) {
//                String accessKey = authDetail.getHash_access_key();
//                String secretKey = authDetail.getHash_secret_key();
//                try {
//                    requestMap.remove("signature");
//                    StringBuilder key = new StringBuilder();
//                    key.append(accessKey).append("|");
//                    key.append(getJSON(requestMap));
//                    key.append("|").append(secretKey);
//                    return EncryptionUtil.decryptSHA256Filter(key.toString());
//                } catch (CipherException e1) {
//                    e1.printStackTrace();
//                    return null;
//                }
//            }
//        }
//        return null;
//    }

    private static String getJSON(Map<String, Object> map) {
        StringBuilder result = new StringBuilder();
        while (map.values().remove(null)) ;
        TreeMap<String, Object> treeMap = new TreeMap<>(map);
        for (Map.Entry<String, Object> entry : treeMap.entrySet()) {
            if (entry.getValue() instanceof Map) {
                result.append(entry.getKey()).append("=").append("{");
                String s = getJSON((Map<String, Object>) entry.getValue());
                result.append(s, 0, s.length() - 1);
                result.append("};");
            } else if (entry.getValue() instanceof List) {
                List list = (List) entry.getValue();
                if (list.size() > 0) {
                    list.sort((o1, o2) -> {
                        Map bo1 = (Map) o1;
                        Map bo2 = (Map) o2;
                        return Integer.compare(Integer.parseInt(bo1.getOrDefault("id", 0).toString()), Integer.parseInt(bo2.getOrDefault("id", 0).toString()));
                    });
                    LinkedList<Map<String, Object>> subList = new LinkedList<>();
                    for (Object itemsMap : list)
                        subList.add(new TreeMap<>((Map<String, Object>) itemsMap));
                    StringBuilder keyItems = new StringBuilder("[");
                    for (Map<String, Object> subMap : subList) {
                        keyItems.append("{");
                        keyItems.append(getJSON(subMap));
                        keyItems.append("};");
                    }
                    keyItems = new StringBuilder(keyItems.substring(0, keyItems.length() - 1));
                    keyItems.append("]");
                    result.append(entry.getKey()).append("=").append(keyItems.toString()).append(";");
                } else {
                    result.append(entry.getKey()).append("=[];");
                }
            } else {
                result.append(entry.getKey()).append("=").append(entry.getValue()).append(";");
            }
        }
        return result.toString();
    }

    public static String generateChecksum(String accessKey, String secretKey, String generatedCheckSumString) throws NoSuchAlgorithmException {
        StringBuilder inputStringBuilder = new StringBuilder().append(accessKey);
        inputStringBuilder.append("|");
        inputStringBuilder.append(generatedCheckSumString);
        inputStringBuilder.append("|").append(secretKey);
        logger.info(" Generated checksum String {}", inputStringBuilder.toString());
        byte[] hashseq = inputStringBuilder.toString().getBytes();
        StringBuffer hexString = new StringBuffer();
        MessageDigest algorithm = MessageDigest.getInstance("SHA-256");
        algorithm.reset();
        algorithm.update(hashseq);
        byte[] messageDigest = algorithm.digest();
        for (int i = 0; i < messageDigest.length; i++) {
            String hex = Integer.toHexString(0xFF & messageDigest[i]);
            if (hex.length() == 1)
                hexString.append("0");
            hexString.append(hex);
        }
        return hexString.toString();
    }
    

    public static String generateChecksum(Map<String, Object> map) {
        StringBuilder keyItems = new StringBuilder();
        if (null != map && map.size() > 0) {
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                keyItems = keyItems.append(entry.getKey() + "=").append(entry.getValue()).append(";");
            }
            keyItems = new StringBuilder(keyItems.substring(0, keyItems.length() - 1));
            keyItems = keyItems.append(";");
        }
        return keyItems.toString();
    }

    public static String generateCheckSum(String accessKey, String secretKey, TreeMap<String, Object> requestMap) throws NoSuchAlgorithmException {
        while (requestMap.values().remove(null))
            ;
        requestMap.remove(SIGNATURE);
        String generatedChecksum = generateChecksum(requestMap);
        generatedChecksum = generateChecksum(accessKey, secretKey, generatedChecksum);
        return generatedChecksum;
    }

    public static boolean isValidCheckSum(String accessKey, String secretKey, Map<String, Object> inputMap, String requestedCheckSum) {
        String checkSum = null;
        TreeMap<String, Object> tree = null;
        try {
            tree = new TreeMap<String, Object>(inputMap);
            logger.info(" Generating checksum for {}", tree.toString());
            checkSum = generateCheckSum(accessKey, secretKey, tree);
            logger.info(" Generated checksum {}", checkSum);
            if (null != checkSum && checkSum.equalsIgnoreCase(requestedCheckSum)) {
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }
    
    public static String generateChecksum(String generatedCheckSumString) throws NoSuchAlgorithmException {
        logger.info(" Generated checksum String {}", generatedCheckSumString);
        byte[] hashseq = generatedCheckSumString.getBytes();
        StringBuffer hexString = new StringBuffer();
        MessageDigest algorithm = MessageDigest.getInstance("SHA-256");
        algorithm.reset();
        algorithm.update(hashseq);
        byte[] messageDigest = algorithm.digest();
        for (int i = 0; i < messageDigest.length; i++) {
            String hex = Integer.toHexString(0xFF & messageDigest[i]);
            if (hex.length() == 1)
                hexString.append("0");
            hexString.append(hex);
        }
        return hexString.toString();
    }
    
    
    
}