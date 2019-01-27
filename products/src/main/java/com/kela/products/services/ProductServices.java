package com.kela.products.services;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.kela.products.beans.*;
import com.kela.products.web.service.HttpsConnectionManager;
import com.kela.products.beans.sort.SortProductOnPriceReduction;
import com.kela.common.response.Response;
import com.kela.common.response.error.ErrorMessage;

public class ProductServices {
	
	private static final Logger LOGGER = Logger.getLogger(ProductServices.class);
	private static Map<String,String> _currencySymbol;
	private static Map<String,String> _rgbHEXList;
	
	static {
		// I will load these information from database or through a 
		// REST service call in real life environment
		_currencySymbol = new HashMap<String,String>();
		_rgbHEXList = new HashMap<String,String>();
		//just for this assignment I opted two values
		_currencySymbol.put("GBP", "£");
		_currencySymbol.put("USD", "$");
		
		//Just for this assignment I am using just 12 colors
		_rgbHEXList.put("White", "FFFFFF");
		_rgbHEXList.put("Silver", "C0C0C0");
		_rgbHEXList.put("Gray", "808080");
		_rgbHEXList.put("Black", "000000");
		_rgbHEXList.put("Red", "FF0000");
		_rgbHEXList.put("Maroon", "800000");
		_rgbHEXList.put("Yellow", "FFFF00");
		_rgbHEXList.put("Olive", "808000");
		_rgbHEXList.put("Lime", "00FF00");
		_rgbHEXList.put("Green", "008000");
		_rgbHEXList.put("Aqua", "00FFFF");
		_rgbHEXList.put("Teal", "008080");
		_rgbHEXList.put("Blue", "0000FF");
		_rgbHEXList.put("Navy", "000080");
		_rgbHEXList.put("Fuchsia", "FF00FF");
		_rgbHEXList.put("Purple", "800080");
		_rgbHEXList.put("French Blue", "0072BB");
		_rgbHEXList.put("Khaki", "C3B091");		
		_rgbHEXList.put("Orange", "FFA500");
		_rgbHEXList.put("Pink", "FFC0CB");	
		
	}
	
	public static boolean isInteger(String val) {	
		if ( !(val.isEmpty()) && ((val.indexOf(".")) < 0) )
			return true;
		
		return false;
	}	
	
	public static String getRGBColor(String basicColor) {
		return _rgbHEXList.get(basicColor);
	}
	
	public static String adjustValue(String val) {
		LOGGER.debug("In adjustValue :" + val);
		try {
				Integer price = Integer.parseInt(val);
				if ( price < 10 ) {
					Double dprice = Double.parseDouble(price.toString());
					val = dprice.toString();
				}
		}
		catch(Exception e) {
			
		}
		LOGGER.debug("Exit adjustValue :" + val);
		return val;
	}
	
	public static String getCurrencySymbol(String symbol) {
		
		return _currencySymbol.get(symbol);
	}
	
	public static String round(Double d) {
		DecimalFormat df = new DecimalFormat("#.##");
		df.setRoundingMode(RoundingMode.CEILING);
		String rv = df.format(d);
		return rv;
	}
	
	//The array of products should only contain products with a price reduction	
	public static boolean isPriceReduced(String was, String now) {
		LOGGER.debug("In isPriceReduuced - was : " +was +" now : " +now);
		if(was.isEmpty() || now.isEmpty())
			return false;
		try {
				Double wasd = Double.parseDouble(was);
				Double nowd = Double.parseDouble(now);
				if( (wasd - nowd) > 0 ) {
					return true;
				}
		}catch(Exception e) {
			LOGGER.error("Exception : " + e.toString());
		}
		LOGGER.debug("Exit isPriceReduuced");
		return false;
	}	
	//and should 
	//be sorted to show the highest price reduction first. Price reduction is 
	//calculated using price.was - price.now.
	public static List<Product> sortProductList(List<Product> prodList){
		Collections.sort(prodList, new SortProductOnPriceReduction());
		return prodList;
	}
	
	public static ArrayList<ColorSwatches> getColorSwatchesList(JSONArray colorSwatches) {
		LOGGER.debug("In getColorSwatchesList");
		ArrayList<ColorSwatches> csl = new ArrayList<ColorSwatches>();
		
		Iterator coloriter = colorSwatches.iterator();		
		
		while(coloriter.hasNext()) {
    		try {
		    		ColorSwatches cs = new ColorSwatches();
		    		JSONObject jc = (JSONObject)coloriter.next();
		    		String color = jc.getString("color");
		    		String skuId = jc.getString("skuId");
		    		cs.setColor(color);
		    		cs.setSkuid(skuId);
		    		String rgbColor = ProductServices.getRGBColor(jc.getString("basicColor"));
		    		cs.setRgbColor(rgbColor);
		    		csl.add(cs);	
    		}catch(Exception e) {
    			LOGGER.error("Exception : " + e.toString());
    		}
    	}
		LOGGER.debug("Exit getColorSwatchesList");
		return csl;
	}
	
	public static String getLabelType(JSONObject price, String labelType) {
		LOGGER.debug("In getLabelType");
		String label="";
		try {		
			//ShowWasThenNow - in which case return a string saying “Was £x.xx, then £y.yy, now £z.zzz”. If the original price.then2 is 
			//not empty use that for the
			//“then” price otherwise use the then1 price. If the then1 price is also empty then don’t show the “then” price.		
			if (labelType.equals("ShowWasThenNow")) {
				String was = price.get("was").toString();
				String then = price.get("then2").toString();
				if (then.isEmpty())
					then = price.get("then1").toString();
				String now = price.get("now").toString();
				String currencysymbol = getCurrencySymbol(price.get("currency").toString());
				label = "Was " + currencysymbol + was;
				if( !(then.isEmpty()) )
						label = label + ", then " + currencysymbol + then;
				
				label = label + ", now " + currencysymbol + now;
			}
			//ShowPercDscount  - in which case return “x% off - now £y.yy”.
			//If the query parm is not set default to use ShowWasNow format.
			//In all cases use the price formatting as described for nowPrice.
			else if (labelType.equals("ShowPercDscount")) {
				String was = price.get("was").toString();
				String now = price.get("now").toString();
				String currencysymbol = getCurrencySymbol(price.get("currency").toString());
				Double wasp = Double.parseDouble(was);				
				Double nowp = Double.parseDouble(now);
				Double dif = wasp - nowp;
				Double pp = dif/wasp * 100;
				label = ProductServices.round(pp) + "% off - now " + currencysymbol + now;			
			}		
			//ShowWasNow - in which case return a string saying “Was £x.xx, now £y.yyy”
			//This is a default value we don't need to compare
			else {
					String was = price.get("was").toString();
					String now = price.get("now").toString();
					String currencysymbol = getCurrencySymbol(price.get("currency").toString());
					label = "Was " + currencysymbol + was + ", now " + currencysymbol + now;
			}	
		}catch(Exception e) {
			LOGGER.error("Exception : " + e.toString());
		}
		
		LOGGER.debug("In getLabelType");
		return label;
	}
	
	public static List<Product> getProductList(JSONArray jProductArray, String requestParam) {				
		LOGGER.info("In getProductList");
		List<Product> productList = new ArrayList<Product>();
		
		Iterator iter = jProductArray.iterator();
		
	    while ( iter.hasNext()) {	
	    	
	    	JSONObject jo = (JSONObject)iter.next();
	    	JSONObject price = jo.getJSONObject("price");
	    	
	    	String was = price.get("was").toString();
	    	String nowPrice = price.get("now").toString();
	    	
	    	//if price is empty or did not change, do not consider that item
	    	try {
			    	if (!ProductServices.isPriceReduced(was, nowPrice))
			    		continue;
	    	}catch(Exception e) {
				LOGGER.error("Exception : " + e.toString());
	    	}
	    				    	
	    	Product product = new Product();
	    	
	    	//check if price is integer value and less than 10
	    	//convert it to decimal value			    	
	    	if (ProductServices.isInteger(nowPrice)){
	    		nowPrice = ProductServices.adjustValue(nowPrice);
	    	}
	    	
	    	product.CalculateReduction(was, nowPrice);
	    	
	    	String symbol = ProductServices.getCurrencySymbol(price.get("currency").toString());
	    	nowPrice = symbol + nowPrice;			    			
			product.setNowPrice(nowPrice);
			
			String priceLabel = ProductServices.getLabelType(price,requestParam);
			product.setPriceLabel(priceLabel);
			product.setProductId(jo.get("productId").toString());
			product.setTitle(jo.get("title").toString());
			
	    	JSONArray colorSwatches = jo.getJSONArray("colorSwatches");
	    	
	    	ArrayList<ColorSwatches> csl = getColorSwatchesList(colorSwatches);
	    	product.setColorSwatches(csl);
	    	productList.add(product);
	   } 
	    LOGGER.info("Exit getProductList");
		return productList;
	}
	
	public static Response getSortedProducts(String url, String requestParam) {
		LOGGER.info("In getSortedProducts");
		String response;
		
		try {
			LOGGER.info("Getting response from remote server....");
			response = HttpsConnectionManager.sendGETmsg(url);
		}catch(Exception e) {
			ErrorMessage errmsg = new ErrorMessage();
			errmsg.setErrorMessage("Exception : " + e.toString());
			LOGGER.error("Exception while getting response from remote server: " + e.toString());
			return errmsg;
		}
		
		JSONObject jsonObj;
		JSONArray jProductArray;
		try {
			jsonObj = new JSONObject(response);
			jProductArray= jsonObj.getJSONArray("products"); 
		}catch(Exception e) {
			ErrorMessage errmsg = new ErrorMessage();
			errmsg.setErrorMessage("Exception : " + e.toString());
			LOGGER.error("Exception : " + e.toString());
			return errmsg;
		}
		List<Product> productList = getProductList(jProductArray,requestParam);
		Products products = new Products();
		products.setProduct(ProductServices.sortProductList(productList));
		LOGGER.info("Exit getSortedProducts");
		return products;
	}
	
	//Below method is only for testing purpose to see the entire list of product
	public static Response getAllProducts(String url, String requestParam) {
		LOGGER.info("In getAllProducts");
		String response = HttpsConnectionManager.sendGETmsg(url);
		
		if ( response.contains("Exception")) {
			ErrorMessage errmsg = new ErrorMessage();
			errmsg.setErrorMessage(response);
		}
		
		JSONObject jsonObj;
		List<Product> productList = new ArrayList<Product>();
		try {
				jsonObj = new JSONObject(response);
			    JSONArray jarray = jsonObj.getJSONArray("products"); 
			    Iterator iter = jarray.iterator();
			    int i = 10;
			    while ( iter.hasNext()) {	
			    	
			    	JSONObject jo = (JSONObject)iter.next();
			    	JSONObject price = jo.getJSONObject("price");
	
			    	String nowPrice = price.get("now").toString();
			    			    	  	
			    	Product product = new Product();
			    	
			    	//check if price is integer value and less than 10
			    	//convert it to decimal value	
			    	try {
					    	if (ProductServices.isInteger(nowPrice)){
					    		nowPrice = ProductServices.adjustValue(nowPrice);
					    	}
			    	}
			    	catch(Exception e) {
			    		LOGGER.error("Exception : " + e.toString());
			    	}
			    	
			    	String symbol = ProductServices.getCurrencySymbol(price.get("currency").toString());
			    	nowPrice = symbol + nowPrice;			    			
					product.setNowPrice(nowPrice);
					
					String priceLabel = ProductServices.getLabelType(price,requestParam);
					product.setPriceLabel(priceLabel);
					product.setProductId(jo.get("productId").toString());
					product.setTitle(jo.get("title").toString());
					
			    	JSONArray colorSwatches = jo.getJSONArray("colorSwatches");
			    	
			    	ArrayList<ColorSwatches> csl = getColorSwatchesList(colorSwatches);
			    	product.setColorSwatches(csl);
			    	productList.add(product);
			    	
			    	product.setColorSwatches(csl);
			    	productList.add(product);
			   }
			   
			   
		}catch(Exception e) {
			ErrorMessage errmsg = new ErrorMessage();
			errmsg.setErrorMessage("Exception : " + e.toString());
			LOGGER.error("Exception : " + e.toString());
			return errmsg;
		}
		
		Products products = new Products();
		products.setProduct(productList);
		LOGGER.info("Exit getAllProducts");
		return products;
	}


}
