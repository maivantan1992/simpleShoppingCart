package com.assignment.service.dto;


import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;


import java.util.Objects;

/**
 * A DTO for the Cart entity.
 */
public class CartDTO implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1422591324322311354L;

	private Long id;
    
    private Set<CartItemDTO> cartItems = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Set<CartItemDTO> getCartItems() {
		return cartItems;
	}

	public void setCartItems(Set<CartItemDTO> cartItems) {
		this.cartItems = cartItems;
	}
	
	public BigDecimal getTotalPrice() {
		BigDecimal totalPrice = BigDecimal.ZERO;
		for(CartItemDTO cartItem: cartItems) {
			totalPrice = totalPrice.add(cartItem.getProduct().getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
		}
		return totalPrice;
	}

	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CartDTO cartDTO = (CartDTO) o;
        if(cartDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), cartDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "CartDTO{" +
            "id=" + getId() +
            "}";
    }
}
