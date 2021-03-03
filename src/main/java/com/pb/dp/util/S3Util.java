package com.pb.dp.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.pb.dp.util.HttpUtil;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
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

    public static String localFileUpload(MultipartFile file) {
        InputStream inputStream = null;
        OutputStream outputStream = null;

        String rootPath = System.getProperty("user.dir");
        File dir = new File(rootPath + File.separator + "webapp"+File.separator+"res"+File.separator+"img");
        if (!dir.exists())
            dir.mkdirs();
        String fileName = file.getOriginalFilename();
        File newFile = new File(dir.getAbsolutePath() + File.separator + fileName);

        try {
            inputStream = file.getInputStream();

            if (!newFile.exists()) {
                newFile.createNewFile();
            }
            outputStream = new FileOutputStream(newFile);
            int read = 0;
            byte[] bytes = new byte[1024];

            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return newFile.getAbsolutePath();
    }
}