package com.assignment.web.rest.errors;

import com.assignment.domain.Product;

public class NotEnoughProductQuantityException extends BadRequestAlertException {

	public NotEnoughProductQuantityException() {
		super("Not enough product quantity", Product.class.getName(), "notEnoughProductQuantity");
	}

}
