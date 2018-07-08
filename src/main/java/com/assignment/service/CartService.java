package com.assignment.service;

import com.assignment.domain.Cart;
import com.assignment.repository.CartRepository;
import com.assignment.service.dto.CartDTO;
import com.assignment.service.mapper.CartMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * Service Implementation for managing Cart.
 */
@Service
@Transactional
public class CartService {

    private final Logger log = LoggerFactory.getLogger(CartService.class);

    private final CartRepository cartRepository;

    private final CartMapper cartMapper;

    public CartService(CartRepository cartRepository, CartMapper cartMapper) {
        this.cartRepository = cartRepository;
        this.cartMapper = cartMapper;
    }

    /**
     * Save a cart.
     *
     * @param cartDTO the entity to save
     * @return the persisted entity
     */
    public CartDTO save(CartDTO cartDTO) {
        log.debug("Request to save Cart : {}", cartDTO);
        Cart cart = cartMapper.toEntity(cartDTO);
        cart = cartRepository.save(cart);
        return cartMapper.toDto(cart);
    }

    /**
     * Get all the carts.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<CartDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Carts");
        return cartRepository.findAll(pageable)
            .map(cartMapper::toDto);
    }

    /**
     * Get one cart by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Transactional(readOnly = true)
    public CartDTO findOne(Long id) {
        log.debug("Request to get Cart : {}", id);
        Cart cart = cartRepository.findOne(id);
        return cartMapper.toDto(cart);
    }

    /**
     * Delete the cart by id.
     *
     * @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Cart : {}", id);
        cartRepository.delete(id);
    }
}
