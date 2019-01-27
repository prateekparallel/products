package com.kela.products.beans.sort;

import java.util.Comparator;

import com.kela.products.beans.Product;

public class SortProductOnPriceReduction implements Comparator<Product>{
	
	public int compare(Product a, Product b) 
    { 
		int rtv = -1;
		if (b.reductionAmount() >  a.reductionAmount())
			rtv = 1;
        return rtv; 
    } 

}
