package com.pb.dp.util;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;


import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.Map.Entry;


/**
 * This utility class is used to call interact with various HTTP supported
 * methods to call from different level in the project.
 *
 * @author ranjeet
 *
 */
public class HttpUtil {

	private static final Logger logger = LoggerFactory.getLogger(HttpUtil.class);

	private static RequestConfig requestConfig = null;

	static {
		requestConfig = RequestConfig.custom()
            .setSocketTimeout(3000).setMaxRedirects(6)
            .setConnectTimeout(3000).build();
	}
	/**
	 * This method is used to get the HTML content by provided URL with GET
	 * method.
	 *
	 * @param targetURL
	 * @return
	 * @throws IOException
	 * @throws ClientProtocolException
	 */
	public static String getContentByURL(String targetURL) throws IOException {
		HttpRequestBase request = new HttpGet(targetURL);
		// List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
		// request.setHeader("User-Agent", "JAVA_CLIENT");
		// request.setHeader("Content-Type", "application/json");
		try (CloseableHttpClient client = HttpClients.custom().setDefaultRequestConfig(RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build()).build()) {
			HttpResponse response = client.execute(request);
			return EntityUtils.toString(response.getEntity());
		} catch (IOException e) {
			logger.error("Exception getContentByURL() URI=" + targetURL + " , msg:" + e.getMessage(), e);
			throw e;
		}
	}

	/**
	 * This method is used to get Content by URL with
	 *
	 * @param targetURL
	 * @param nameValPairs
	 * @return
	 */
	public static String getContentByURL(String targetURL, Map<String, String> nameValPairs) {
		try(CloseableHttpClient client = HttpClients.custom().setDefaultRequestConfig(RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build()).build()) {
			HttpRequestBase request;
			List<NameValuePair> urlParameters = new ArrayList<>();
			Iterator<Entry<String, String>> iterator = nameValPairs.entrySet().iterator();
			while (iterator.hasNext()) {
				Entry<String, String> entry = iterator.next();
				urlParameters.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
				logger.debug("Key : " + entry.getKey() + " and Value: " + entry.getValue());
				// System.out.printf("Key : %s and Value: %s %n",
				// entry.getKey(), entry.getValue());
				iterator.remove(); // right way to remove entries from Map,
									// avoids ConcurrentModificationException
			}

			// request.setHeader("User-Agent", "JAVA_CLIENT");
			// request.setHeader("Content-Type", "application/json");
			String query = URLEncodedUtils.format(urlParameters, "utf-8");
			// request.setEntity(new UrlEncodedFormEntity(urlParameters));
			targetURL += query;
			request = new HttpGet(targetURL);
			HttpResponse response = client.execute(request);
			if (response != null) {
				return EntityUtils.toString(response.getEntity());
			}
		} catch (ClientProtocolException e) {
			logger.error("ClientProtocolException getContentByURL() URI=" + targetURL + " , msg:" + e.getMessage(), e);
		} catch (IOException e) {
			logger.error("IOException getContentByURL() URI=" + targetURL + " , msg:" + e.getMessage(), e);
		}
		return null;
	}

	public static Map<String, Object> post(String uri, String jsonPayload) {
		try (CloseableHttpClient httpclient = HttpClients.custom().setDefaultRequestConfig(RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build()).build()) {
			// url with the post data
			HttpPost httpost = new HttpPost(uri);

			// convert parameters into JSON object
			// JSONObject holder = getJsonObjectFromMap(headerparams);

			// passes the results to a string builder/entity
			StringEntity se = new StringEntity(jsonPayload);

			// sets the post request as the resulting string
			httpost.setEntity(se);

			// sets a request header so the page receving the request will know
			// what to do with it
			httpost.setHeader("Accept", "application/json");
			httpost.setHeader("Content-type", "application/json");

			// Handles what is returned from the page
			// /ResponseHandler responseHandler = new BasicResponseHandler();
			HttpResponse httpResponse = httpclient.execute(httpost);
			String responseAsString = "";
			int statusCode = 0;
			if (httpResponse != null) {
				responseAsString = EntityUtils.toString(httpResponse.getEntity());
				statusCode = httpResponse.getStatusLine().getStatusCode();
				logger.debug(responseAsString);
			} else {
				logger.debug("No response receceived from[ " + uri + " ], for payload[ " + jsonPayload + " ]");
			}

			Map<String, Object> responseAttributes = new HashMap<>();
			responseAttributes.put("responseBody", responseAsString);
			responseAttributes.put("status", statusCode);
			return responseAttributes;
//			return ImmutableMap.of("responseBody", responseAsString,"status", statusCode);
		} catch (ClientProtocolException e) {
			logger.error("ClientProtocolException post() URI=" + uri + " , msg:" + e.getMessage(), e);
		} catch (Exception e) {
			logger.error("Exception post() URI=" + uri + " , msg:" + e.getMessage(), e);
		}
		return null;
	}

	/**
	 * This method is used to post on uri with JSON payLoad.
	 *
	 * @param uri
	 * @param jsonPayload
	 * @return
	 * @throws Exception
	 */
	public static int postRequestWithJsonPayload(String uri, String jsonPayload) {
		int statusCode = 0;
		Map<String, Object> response = post(uri, jsonPayload);
		if (response != null) {
			statusCode = response.get("status") != null ? Integer.parseInt(response.get("status").toString()) : statusCode;
			logger.debug(response.toString());
		}
		return statusCode;
	}

	/**
	 * @param uri
	 * @param jsonPayload
	 * @param header
	 * @return
	 * @throws Exception
	 */
	public static Map<String, Object> post(String uri, String jsonPayload, Map<String, String> header) throws Exception {
		Map<String, Object> responseAttributes = new HashMap<>();
		try (CloseableHttpClient httpclient = HttpClients.custom().setDefaultRequestConfig(RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build()).build()){
			// url with the post data
			HttpPost httpost = new HttpPost(uri);

			// passes the results to a string builder/entity
			StringEntity se = new StringEntity(jsonPayload);

			// sets the post request as the resulting string
			httpost.setEntity(se);

			// sets a request header so the page receving the request will know
			// what to do with it
			if (header != null) {
				for (Entry<String, String> entry : header.entrySet()) {
					httpost.setHeader(entry.getKey(), entry.getValue());
				}
			}
			httpost.setHeader("Accept", "application/json");
			httpost.setHeader("Content-type", "application/json");

			// Handles what is returned from the page
			// /ResponseHandler responseHandler = new BasicResponseHandler();
			HttpResponse httpResponse = httpclient.execute(httpost);
			String responseAsString = "";
			int statusCode = 0;
			if (httpResponse != null) {
				responseAsString = EntityUtils.toString(httpResponse.getEntity());
				statusCode = httpResponse.getStatusLine().getStatusCode();
				logger.debug(responseAsString);
			} else {
				logger.debug("No response receceived from[ " + uri + " ], for payload[ " + jsonPayload + " ]");
			}

			responseAttributes.put("responseBody", responseAsString);
			responseAttributes.put("status", statusCode);
		} catch (ClientProtocolException e) {
			logger.error("ClientProtocolException post() URI=" + uri + " , msg:" + e.getMessage(), e);
		} catch (Exception e) {
			logger.error("Exception post() URI=" + uri + " , msg:" + e.getMessage(), e);
		}
		return responseAttributes;
	}

	/**
	 * This method is used to get Content by URL with
	 *
	 * @param targetURL
	 * @return
	 */
	public static Map<String,Object> getContentByteByURL(String targetURL) {
		Map<String, Object> output = new HashMap<>();
		try (CloseableHttpClient client = HttpClients.custom().setDefaultRequestConfig(RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build()).build()) {
			HttpRequestBase request = new HttpGet(targetURL);
			HttpResponse response = client.execute(request);
			if (response != null) {
				output.put("contentType",response.getEntity().getContentType().getValue());
				output.put("mimeType",EntityUtils.getContentMimeType(response.getEntity()));
				output.put("Bytes", EntityUtils.toByteArray(response.getEntity()));
			}
		} catch (ClientProtocolException e) {
			logger.error("ClientProtocolException getContentByteByURL() URI=" + targetURL + " , msg:" + e.getMessage(), e);
		} catch (IOException e) {
			logger.error("IOException getContentByteByURL() URI=" + targetURL + " , msg:" + e.getMessage(), e);
		}
		return output;
	}
	
	public static Map<String,Object> getContentByteByURLWithHeader(String targetURL,Map<String,String> header) {
		Map<String, Object> output = new HashMap<>();
		try (CloseableHttpClient client = HttpClients.custom().setDefaultRequestConfig(RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build()).build()) {
			HttpRequestBase request = new HttpGet(targetURL);
			if (header != null) {
				for (Entry<String, String> entry : header.entrySet()) {
					request.setHeader(entry.getKey(), entry.getValue());
				}
			}
			HttpResponse response = client.execute(request);
			if (response != null && response.getStatusLine().getStatusCode() == 200) {
				output.put("contentType",response.getEntity().getContentType().getValue());
				output.put("mimeType",EntityUtils.getContentMimeType(response.getEntity()));
				output.put("Bytes", EntityUtils.toByteArray(response.getEntity()));
			}
		} catch (ClientProtocolException e) {
			logger.error("ClientProtocolException getContentByteByURL() URI=" + targetURL + " , msg:" + e.getMessage(), e);
		} catch (IOException e) {
			logger.error("IOException getContentByteByURL() URI=" + targetURL + " , msg:" + e.getMessage(), e);
		}
		return output;
	}
	

	public static Map<String, Object> postByte(String uri, byte[] bytes) {
		HttpResponse httpResponse;
		Map<String, Object> responseAttributes = new HashMap<>();

		try(CloseableHttpClient httpclient= HttpClients.custom().setDefaultRequestConfig(RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build()).build()		) {

			// url with the post data
			HttpPost httpost = new HttpPost(uri);

			// convert parameters into JSON object
			// JSONObject holder = getJsonObjectFromMap(headerparams);

			// passes the results to a string builder/entity
			//StringEntity se = new StringEntity(bytes);

			// sets the post request as the resulting string
			httpost.setEntity(new ByteArrayEntity(bytes));

			// sets a request header so the page receiving the request will know
			// what to do with it
			httpost.setHeader("Accept", "multipart/form-data");
			httpost.setHeader("Content-type", "multipart/form-data");

			// Handles what is returned from the page
			// /ResponseHandler responseHandler = new BasicResponseHandler();
			httpResponse = httpclient.execute(httpost);
			String responseAsString = "";
			int statusCode = 0;
			if (httpResponse != null) {
				responseAsString = EntityUtils.toString(httpResponse.getEntity());
				statusCode = httpResponse.getStatusLine().getStatusCode();
				logger.debug(responseAsString);
			} else {
				logger.debug("No response receceived from[ " + uri + " ], for payload[ " + uri + " ]");
			}

			responseAttributes.put("responseBody", responseAsString);
			responseAttributes.put("status", statusCode);
		} catch (ClientProtocolException e) {
			logger.error("ClientProtocolException postByte() URI=" + uri + " , msg:" + e.getMessage());
		} catch (Exception e) {
			logger.error("Exception postByte() URI=" + uri + " , msg:" + e.getMessage());
		}
		return responseAttributes;
	}

//	public static void asyncPost(String uri, final String jsonPayload) throws Exception {
//		try (CloseableHttpAsyncClient httpclient = HttpAsyncClients.custom().setDefaultRequestConfig(requestConfig).build()) {
//			// Start the client
//			httpclient.start();
//
//			 // url with the post data
//			final HttpPost httpost = new HttpPost(uri);
//
//			// passes the results to a string builder/entity
//			StringEntity se = new StringEntity(jsonPayload);
//
//			// sets the post request as the resulting string
//			httpost.setEntity(se);
//
//			// sets a request header so the page receving the request will know
//			// what to do with it
//
//			httpost.setHeader("Accept", "application/json");
//			httpost.setHeader("Content-type", "application/json");
//
//		    // One most likely would want to use a callback for operation result
//		    final CountDownLatch latch1 = new CountDownLatch(1);
//		   // httpclient.execute(httpost, null);
//		    //final HttpGet request2 = new HttpGet("http://www.apache.org/");
//		    httpclient.execute(httpost, new FutureCallback<HttpResponse>() {
//
//		        public void completed(final HttpResponse response2) {
//		            latch1.countDown();
//		            logger.debug(httpost.getRequestLine() + "->" + response2.getStatusLine());
//		        }
//
//		        public void failed(final Exception ex) {
//		            latch1.countDown();
//		            logger.error(httpost.getRequestLine() + "->" + ex);
//		            LoggerServiceImpl.addErrorLog(LogConstant.APPID_CORESERVICE, LogConstant.ENTITY_FEEDBACK, "addRequest", jsonPayload, ex,
//							LogConstant.ERR_CODE_IS);
//		        }
//
//		        public void cancelled() {
//		            latch1.countDown();
//		            logger.debug(httpost.getRequestLine() + " cancelled");
//		        }
//
//		    });
//		    latch1.await();
//		}
//	}
//
//
//	public static void asyncGet(String uri) throws Exception {
//		try (CloseableHttpAsyncClient httpclient = HttpAsyncClients.createDefault()) {
//			httpclient.start();
//			HttpGet request = new HttpGet(uri);
//			Future<HttpResponse> future = httpclient.execute(request, null);
//			HttpResponse response = future.get();
//			logger.info("Async Get method uri = " + uri + " status is " + response.getStatusLine());
//		}
//	}

	public static byte[] getByteContentFromURL(String strUrl) {
		byte[] byteArray = new byte[1024];
		int size;
		HttpURLConnection conn = null;
		try {
			conn = (HttpURLConnection) new URL(strUrl).openConnection();
			conn.setDoInput(true);
			conn.connect();
			try (InputStream in = conn.getInputStream();
				 ByteArrayOutputStream out = new ByteArrayOutputStream()) {
				while ((size = in.read(byteArray)) != -1) {
					out.write(byteArray, 0, size);
				}
				return out.toByteArray();
			}
		} catch (IOException e) {
			logger.error("Exception getByteContentFromURL() URI=" + strUrl + " , msg:" + e.getMessage());
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}
		return null;
	}
	
	public static Map<String,Object> deleteWithHeader(String targetURL,Map<String,String> header) {
		Map<String, Object> output = new HashMap<>();
		try (CloseableHttpClient client = HttpClients.custom().setDefaultRequestConfig(RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build()).build()) {
			HttpRequestBase request = new HttpDelete(targetURL);
			if (header != null) {
				for (Entry<String, String> entry : header.entrySet()) {
					request.setHeader(entry.getKey(), entry.getValue());
				}
			}
			HttpResponse response = client.execute(request);
			if (response != null) {
				output.put("status",Integer.toString((response.getStatusLine().getStatusCode())));
				output.put("responseBody",EntityUtils.toString(response.getEntity()));
			}
		} catch (ClientProtocolException e) {
			logger.error("ClientProtocolException getContentByteByURL() URI=" + targetURL + " , msg:" + e.getMessage(), e);
		} catch (IOException e) {
			logger.error("IOException getContentByteByURL() URI=" + targetURL + " , msg:" + e.getMessage(), e);
		}
		return output;
	}

	public static Map<String, String> postRequestMultiPart(String uri, File file, String payloadJson, Map<String, String> header) throws Exception {
		HttpResponse httpResponse = null;
		Map<String, String> responseAttributes = new HashMap<String, String>(2);
		CloseableHttpClient httpclient = null;
		HttpPost httpost = null;
		try {

			httpclient = HttpClients.createDefault();
			httpost = new HttpPost(uri);

			if (header != null) {
				for (Entry<String, String> entry : header.entrySet()) {
					httpost.setHeader(entry.getKey(), entry.getValue());
				}
			}

			MultipartEntityBuilder builder = MultipartEntityBuilder.create();
			builder.addTextBody("payloadJSON",  JsonUtil.getJsonStringFromObject(payloadJson), ContentType.TEXT_PLAIN);
			// This attaches the file to the POST:


//			MultipartFile mf = (MultipartFile) requestMap.get("file");
//			builder.addPart("file", mf);
//			builder.addBinaryBody(
//					"file",
//					mf.getInputStream(),
//					ContentType.parse(mf.getContentType()),
//					mf.getOriginalFilename()
//			);
//			HttpEntity multipart = builder.build();
			HttpEntity entity = builder.addPart("file", new FileBody(file)).build();

			httpost.setEntity(entity);
			httpResponse = httpclient.execute(httpost);
			String responseAsString = "";
			int statusCode = 0;
			if (httpResponse != null) {
				responseAsString = EntityUtils.toString(httpResponse.getEntity());
				statusCode = httpResponse.getStatusLine().getStatusCode();
				logger.debug(responseAsString);
			} else {
				logger.debug("No response receceived from[ " + uri + " ], for payload[ " + payloadJson + " ]");
			}

			responseAttributes.put("responseBody", responseAsString);
			responseAttributes.put("status", String.valueOf(statusCode));

		} catch (ClientProtocolException e) {
			logger.error("ClientProtocolException caught wile posting json payload on URI=" + uri + " , msg:" + e.getMessage());
		} catch (Exception e) {
			logger.error("Exception caught wile posting json payload on URI=" + uri + " , msg:" + e.getMessage());
		} finally {
			if (httpost != null) {
				httpost.releaseConnection();
			}
			if (httpclient != null) {
				httpclient.close();
			}
			if (httpResponse != null) {
				httpResponse = null;
			}
		}
		return responseAttributes;
	}

	public static Map<String, String> postRequestMultiPartFile(String uri, Map<String, Object> requestMap, Map<String, String> header) throws IOException {
		HttpResponse httpResponse = null;
		Map<String, String> responseAttributes = new HashMap<String, String>(2);
		// HttpClient httpclient = null;
		CloseableHttpClient httpclient = null;
		HttpPost httpost = null;
		try {
			// instantiates httpclient to make request
			// httpclient = HttpClientBuilder.create().build();
			httpclient = HttpClients.createDefault();
			httpost = new HttpPost(uri);
			
			if (header != null) {
				for (Entry<String, String> entry : header.entrySet()) {
					httpost.setHeader(entry.getKey(), entry.getValue());
				}
			}
			
			MultipartEntityBuilder builder = MultipartEntityBuilder.create();
			builder.addTextBody("payloadJSON",  JsonUtil.getJsonStringFromObject(requestMap.get("payloadJSON")), ContentType.TEXT_PLAIN);
			// This attaches the file to the POST:
			MultipartFile mf = (MultipartFile) requestMap.get("file");
			builder.addBinaryBody(
			    "file",
			    mf.getInputStream(),
			    ContentType.parse(mf.getContentType()),
			    mf.getOriginalFilename()
			);

			HttpEntity multipart = builder.build();
			httpost.setEntity(multipart);
			httpResponse = httpclient.execute(httpost);
			String responseAsString = "";
			int statusCode = 0;
			if (httpResponse != null) {
				responseAsString = EntityUtils.toString(httpResponse.getEntity());
				statusCode = httpResponse.getStatusLine().getStatusCode();
				logger.debug(responseAsString);
			} else {
				logger.debug("No response receceived from[ " + uri + " ], for payload[ " + requestMap.toString() + " ]");
			}

			responseAttributes.put("responseBody", responseAsString);
			responseAttributes.put("status", String.valueOf(statusCode));

		} catch (ClientProtocolException e) {
			logger.error("ClientProtocolException caught wile posting json payload on URI=" + uri + " , msg:" + e.getMessage());
		} catch (Exception e) {
			logger.error("Exception caught wile posting json payload on URI=" + uri + " , msg:" + e.getMessage());
		} finally {
			if (httpost != null) {
				httpost.releaseConnection();
			}
			if (httpclient != null) {
				httpclient.close();
			}
			if (httpResponse != null) {
				httpResponse = null;
			}
		}
		return responseAttributes;
	}
}
