package com.assignment.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.assignment.service.ShoppingCartService;
import com.assignment.service.dto.CartDTO;
import com.assignment.service.dto.CartItemDTO;
import com.assignment.web.rest.errors.BadRequestAlertException;
import com.assignment.web.rest.util.HeaderUtil;
import com.codahale.metrics.annotation.Timed;

import io.github.jhipster.web.util.ResponseUtil;


/**
 * The Class ShoppingCartResource.
 */
@RestController
@RequestMapping("/api")
public class ShoppingCartResource {
	
	/** The log. */
	private final Logger log = LoggerFactory.getLogger(ShoppingCartResource.class);
	
	/** The Constant ENTITY_CART_NAME. */
	private static final String ENTITY_CART_NAME = "cart";
	
	/** The Constant ENTITY_CART_ITEM_NAME. */
	private static final String ENTITY_CART_ITEM_NAME = "cartItem";
	
	/** The shopping cart service. */
	private final ShoppingCartService shoppingCartService;

	/**
	 * Instantiates a new shopping cart resource.
	 *
	 * @param shoppingCartService the shopping cart service
	 */
	public ShoppingCartResource(ShoppingCartService shoppingCartService) {
		this.shoppingCartService = shoppingCartService;
	}

	/**
	 * POST  /shopping-carts : Inits the cart.
	 *
	 * @return the response entity
	 * @throws URISyntaxException the URI syntax exception
	 */
	@PostMapping("/shopping-carts")
    @Timed
    public ResponseEntity<CartDTO> initCart() throws URISyntaxException {
        log.debug("REST request to init cart");
        CartDTO result = shoppingCartService.save(new CartDTO());
        return ResponseEntity.created(new URI("/api/shopping-carts/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_CART_NAME, result.getId().toString()))
            .body(result);
    }
	
	/**
	 * GET  /shopping-carts/{id} : Gets the cart.
	 *
	 * @param id the id
	 * @return the cart
	 */
	@GetMapping("/shopping-carts/{id}")
    @Timed
    public ResponseEntity<CartDTO> getCart(@PathVariable Long id) {
        log.debug("REST request to get Cart : {}", id);
        CartDTO cartDTO = shoppingCartService.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(cartDTO));
    }
	
	 /**
 	 * DELETE  /shopping-carts/{id} : Delete cart.
 	 *
 	 * @param id the id
 	 * @return the response entity
 	 */
 	@DeleteMapping("/shopping-carts/{id}")
    @Timed
    public ResponseEntity<Void> deleteCart(@PathVariable Long id) {
        log.debug("REST request to delete Cart : {}", id);
        shoppingCartService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_CART_NAME, id.toString())).build();
    }
	
	/**
	 * POST  /shopping-carts/add-item : Creates the cart item.
	 *
	 * @param cartItemDTO the cart item DTO
	 * @return the response entity
	 * @throws URISyntaxException the URI syntax exception
	 */
	@PostMapping("/shopping-carts/add-item")
    @Timed
    public ResponseEntity<CartDTO> createCartItem(@Valid @RequestBody CartItemDTO cartItemDTO) throws URISyntaxException {
        log.debug("REST request to add item : {}", cartItemDTO);
        if (cartItemDTO.getId() != null) {
            throw new BadRequestAlertException("A new cartItem cannot already have an ID", ENTITY_CART_ITEM_NAME, "idexists");
        }
        CartDTO result = shoppingCartService.addItem(cartItemDTO);
        return ResponseEntity.created(new URI("/api/cart-items/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_CART_ITEM_NAME, result.getId().toString()))
            .body(result);
    }
	
	/**
	 * PUT  /shopping-carts/update-item : Update cart item.
	 *
	 * @param cartItemDTO the cart item DTO
	 * @return the response entity
	 * @throws URISyntaxException the URI syntax exception
	 */
	@PutMapping("/shopping-carts/update-item")
    @Timed
    public ResponseEntity<CartDTO> updateCartItem(@Valid @RequestBody CartItemDTO cartItemDTO) throws URISyntaxException {
        log.debug("REST request to update item : {}", cartItemDTO);
        if (cartItemDTO.getId() == null) {
            return createCartItem(cartItemDTO);
        }
        CartDTO result = shoppingCartService.updateItem(cartItemDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_CART_ITEM_NAME, cartItemDTO.getId().toString()))
            .body(result);
    }
	
	/**
	 * DELETE  /shopping-carts/delete-item/{id} : Delete cart item.
	 *
	 * @param id the id
	 * @return the response entity
	 * @throws URISyntaxException the URI syntax exception
	 */
	@DeleteMapping("/shopping-carts/delete-item/{id}")
    @Timed
    public ResponseEntity<CartDTO> deleteCartItem(@Nonnull @PathVariable Long id) throws URISyntaxException {
        log.debug("REST request to delete item : {}", id);
        CartDTO result = shoppingCartService.deleteItem(id);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityDeletionAlert(ENTITY_CART_ITEM_NAME, id.toString()))
            .body(result);
    }
	
	
}
