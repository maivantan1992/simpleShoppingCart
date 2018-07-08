package com.assignment.service.mapper;

import com.assignment.domain.*;
import com.assignment.service.dto.CartDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity Cart and its DTO CartDTO.
 */
@Mapper(componentModel = "spring", uses = {CartItemMapper.class})
public interface CartMapper extends EntityMapper<CartDTO, Cart> {


    @Mapping(target = "cartItems", ignore = true)
    Cart toEntity(CartDTO cartDTO);
    
    default Cart fromId(Long id) {
        if (id == null) {
            return null;
        }
        Cart cart = new Cart();
        cart.setId(id);
        return cart;
    }
}
