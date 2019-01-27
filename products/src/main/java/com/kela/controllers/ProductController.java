package com.kela.controllers;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kela.products.services.*;
import com.kela.products.web.service.HttpsConnectionManager;
import com.kela.common.config.ApplicationConfig;
import com.kela.common.response.Response;

@Controller
public class ProductController {
	  private static final Logger LOGGER = Logger.getLogger(HttpsConnectionManager.class);
	  
	  @RequestMapping(method = RequestMethod.GET, value="/products/600001506")
	  @ResponseBody
	  public Response getSortedProducts(@RequestParam(name="labelType", required=false, defaultValue="ShowWasNow") String labelType) {
		  LOGGER.info("In getSortedProducts with labelType : " + labelType);
		  ApplicationConfig ac = ApplicationConfig.getInstance();
		  return ProductServices.getSortedProducts(ac.getHTTPS_SERV_URL(),labelType);
	  }
	 
	  @RequestMapping(method = RequestMethod.GET, value="/products/600001506/All")
	  @ResponseBody
	  public Response getAlldProducts(@RequestParam(name="labelType", required=false, defaultValue="ShowWasNow") String labelType) {
		  LOGGER.info("In getAlldProducts with labelType : " + labelType);
		  ApplicationConfig ac = ApplicationConfig.getInstance();
		  return ProductServices.getAllProducts(ac.getHTTPS_SERV_URL(),labelType);
	  }

}
