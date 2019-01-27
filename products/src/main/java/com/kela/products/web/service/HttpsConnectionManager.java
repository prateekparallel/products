package com.kela.products.web.service;


import java.net.URL;
import javax.net.ssl.HttpsURLConnection;
import org.apache.log4j.Logger;
import org.apache.cxf.helpers.IOUtils;


public class HttpsConnectionManager {
	
	static int _responseCode = -1;
	
	private static final Logger LOGGER = Logger.getLogger(HttpsConnectionManager.class);
	
	static HttpsURLConnection connection_ = null;
    
    static public boolean setHTTPSConnection(String targetURL) {
    	LOGGER.info("In setSSLConnection");
    	URL url = null;
    	
    				try {
    					url = new URL(null, targetURL,
    							new sun.net.www.protocol.https.Handler());
    				} catch (Exception e1) {
    					LOGGER.error("MalformedURLException occurred " + e1.getMessage());
    				}
    				
    			try {
    				connection_ = (HttpsURLConnection) url.openConnection();
    	            connection_.setDoOutput( true );
    	            connection_.setRequestProperty( "Content-Type", "text/xml"  );
    	            connection_.connect();
    				return true;
    			} catch (Exception e) {
    				LOGGER.error("Exception occurred while establishing connection to SSL server. Error :"
    						+ e.getMessage());
    				connection_.disconnect();
    				connection_ = null;
    				return false;
    			}
    	}
    
    
    static public String sendGETmsg(String url) {
		
    	LOGGER.info("In sendGETmsg");
		String response = null;
		int retcode = 500;
		if(!HttpsConnectionManager.setHTTPSConnection(url)) {
	    	   	response = "Failed to setHTTPSConnection ";	
	    	   	LOGGER.error(response);
	   			return response;
	       }
		try {
				retcode = connection_.getResponseCode();
				LOGGER.info("In sendGETmsg response code :" + retcode);
				if(retcode != 401 && retcode != 500) {
				    response = IOUtils.toString(connection_.getInputStream());
				}
				else {
					response="Failed to connect to remote server :";
					LOGGER.error(response);
					return response;
				}
				
		}catch(Exception e) {
			connection_.disconnect();
			response="Exception while connecting to remote server";
			LOGGER.error(response);
			e.printStackTrace();
			return response;
		}
		return response;
	}


}
