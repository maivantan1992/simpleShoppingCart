package com.assignment.web.rest.errors;

import com.assignment.domain.Cart;

public class CartItemNotExistException extends BadRequestAlertException {

	public CartItemNotExistException() {
		super("Cart not existed", Cart.class.getName(), "cartNotExisted");
	}

}
