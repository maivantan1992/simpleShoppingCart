package com.assignment.web.rest.errors;

import com.assignment.domain.Product;

public class ProductNotExistException extends BadRequestAlertException {

	public ProductNotExistException() {
		super("Product not existed", Product.class.getName(), "productNotExisted");
	}

}
