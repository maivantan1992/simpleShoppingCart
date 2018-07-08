package com.assignment.service;

import com.assignment.domain.CartItem;
import com.assignment.repository.CartItemRepository;
import com.assignment.service.dto.CartItemDTO;
import com.assignment.service.mapper.CartItemMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * Service Implementation for managing CartItem.
 */
@Service
@Transactional
public class CartItemService {

    private final Logger log = LoggerFactory.getLogger(CartItemService.class);

    private final CartItemRepository cartItemRepository;

    private final CartItemMapper cartItemMapper;

    public CartItemService(CartItemRepository cartItemRepository, CartItemMapper cartItemMapper) {
        this.cartItemRepository = cartItemRepository;
        this.cartItemMapper = cartItemMapper;
    }

    /**
     * Save a cartItem.
     *
     * @param cartItemDTO the entity to save
     * @return the persisted entity
     */
    public CartItemDTO save(CartItemDTO cartItemDTO) {
        log.debug("Request to save CartItem : {}", cartItemDTO);
        CartItem cartItem = cartItemMapper.toEntity(cartItemDTO);
        cartItem = cartItemRepository.save(cartItem);
        return cartItemMapper.toDto(cartItem);
    }

    /**
     * Get all the cartItems.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<CartItemDTO> findAll(Pageable pageable) {
        log.debug("Request to get all CartItems");
        return cartItemRepository.findAll(pageable)
            .map(cartItemMapper::toDto);
    }

    /**
     * Get one cartItem by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Transactional(readOnly = true)
    public CartItemDTO findOne(Long id) {
        log.debug("Request to get CartItem : {}", id);
        CartItem cartItem = cartItemRepository.findOne(id);
        return cartItemMapper.toDto(cartItem);
    }

    /**
     * Delete the cartItem by id.
     *
     * @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete CartItem : {}", id);
        cartItemRepository.delete(id);
    }
}
