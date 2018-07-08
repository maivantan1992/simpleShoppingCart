package com.assignment.web.rest.errors;

import com.assignment.domain.CartItem;

public class CartNotExistException extends BadRequestAlertException {

	public CartNotExistException() {
		super("Cart item not existed", CartItem.class.getName(), "cartItemNotExisted");
	}

}
