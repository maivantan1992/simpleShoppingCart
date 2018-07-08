package com.assignment.service.dto;


import javax.validation.constraints.*;

import com.assignment.domain.Product;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A DTO for the CartItem entity.
 */
public class CartItemDTO implements Serializable {

    private Long id;

    @NotNull
    @Min(1)
    private Integer quantity;

    @NotNull
    private ProductDTO product;

    @NotNull
    private Long cartId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Long getCartId() {
        return cartId;
    }

    public ProductDTO getProduct() {
		return product;
	}

	public void setProduct(ProductDTO product) {
		this.product = product;
	}

	public void setCartId(Long cartId) {
        this.cartId = cartId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CartItemDTO cartItemDTO = (CartItemDTO) o;
        if(cartItemDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), cartItemDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "CartItemDTO{" +
            "id=" + getId() +
            ", quantity=" + getQuantity() +
            "}";
    }
}
