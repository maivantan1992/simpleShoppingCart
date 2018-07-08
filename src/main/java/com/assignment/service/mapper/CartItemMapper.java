package com.assignment.service.mapper;

import com.assignment.domain.*;
import com.assignment.service.dto.CartItemDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity CartItem and its DTO CartItemDTO.
 */
@Mapper(componentModel = "spring", uses = {ProductMapper.class, CartMapper.class})
public interface CartItemMapper extends EntityMapper<CartItemDTO, CartItem> {

    @Mapping(source = "cart.id", target = "cartId")
    CartItemDTO toDto(CartItem cartItem);

    @Mapping(source = "cartId", target = "cart")
    CartItem toEntity(CartItemDTO cartItemDTO);

    default CartItem fromId(Long id) {
        if (id == null) {
            return null;
        }
        CartItem cartItem = new CartItem();
        cartItem.setId(id);
        return cartItem;
    }
}
