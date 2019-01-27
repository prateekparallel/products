package com.kela.products.beans;

import java.util.ArrayList;

public class Product {
	
	private String productId;
	private String title;
	private ArrayList<ColorSwatches> colorSwatches;
	private String nowPrice;
	private String priceLabel;
	private double totalReduction;
	
	public String getProductId() {
		return productId;
	}
	public void setProductId(String productId) {
		this.productId = productId;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public ArrayList<ColorSwatches> getColorSwatches() {
		return colorSwatches;
	}
	public void setColorSwatches(ArrayList<ColorSwatches> colorSwatches) {
		this.colorSwatches = colorSwatches;
	}
	public String getNowPrice() {
		return nowPrice;
	}
	public void setNowPrice(String nowPrice) {
		this.nowPrice = nowPrice;
	}
	public String getPriceLabel() {
		return priceLabel;
	}
	public void setPriceLabel(String priceLabel) {
		this.priceLabel = priceLabel;
	}
	
	//Price reduction is calculated using price.was - price.now
	public void CalculateReduction(String was, String now) {
		try {
				double dwas = Double.parseDouble(was);
				double dnow = Double.parseDouble(now);
				totalReduction = dwas - dnow;
		}catch(Exception e) {}
		
	}
	
	public Double reductionAmount() {
		return totalReduction;
	}
	
}
