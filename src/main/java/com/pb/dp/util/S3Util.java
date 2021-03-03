package com.pb.dp.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.pb.dp.util.HttpUtil;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.Map.Entry;


public class S3Util{

    private static final Logger logger = LoggerFactory.getLogger(HttpUtil.class);

    public static File convertMultiPartToFile(MultipartFile file ) throws IOException
    {
        File convFile = new File( file.getOriginalFilename() );
        FileOutputStream fos = new FileOutputStream( convFile );
        fos.write( file.getBytes() );
        fos.close();
        return convFile;
    }

    public static String upload(String uri, MultipartFile file, String jsonPayload, Map < String, String > header) throws Exception {
        Map<String, Object>  requestMap = new HashMap<String, Object>();
        File convertedFile = convertMultiPartToFile(file);
        Map<String, String> responseMap  = HttpUtil.postRequestMultiPart(uri, convertedFile, jsonPayload, header);
        return "test";
    }
}