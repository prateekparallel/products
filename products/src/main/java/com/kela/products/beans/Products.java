package com.kela.products.beans;

import java.util.List;

import com.kela.common.response.Response;

public class Products extends Response{
	
	private List<Product> product;

	public List<Product> getProduct() {
		return product;
	}

	public void setProduct(List<Product> product) {
		this.product = product;
	}

}
