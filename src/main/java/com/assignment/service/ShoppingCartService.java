package com.assignment.service;

import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.assignment.domain.Cart;
import com.assignment.domain.CartItem;
import com.assignment.domain.Product;
import com.assignment.repository.CartItemRepository;
import com.assignment.repository.CartRepository;
import com.assignment.repository.ProductRepository;
import com.assignment.service.dto.CartDTO;
import com.assignment.service.dto.CartItemDTO;
import com.assignment.service.mapper.CartItemMapper;
import com.assignment.service.mapper.CartMapper;
import com.assignment.web.rest.errors.CartItemNotExistException;
import com.assignment.web.rest.errors.CartNotExistException;
import com.assignment.web.rest.errors.NotEnoughProductQuantityException;
import com.assignment.web.rest.errors.ProductNotExistException;


/**
 * The Class ShoppingCartService.
 */
@Service
@Transactional
public class ShoppingCartService {

	private final Logger log = LoggerFactory.getLogger(ShoppingCartService.class);

	private final CartRepository cartRepository;

	private final CartMapper cartMapper;

	private final CartItemRepository cartItemRepository;

	private final CartItemMapper cartItemMapper;
	
	private final ProductRepository productRepository;

	/**
	 * Instantiates a new shopping cart service.
	 *
	 * @param cartRepository the cart repository
	 * @param cartMapper the cart mapper
	 * @param cartItemRepository the cart item repository
	 * @param cartItemMapper the cart item mapper
	 * @param productRepository the product repository
	 */
	public ShoppingCartService(CartRepository cartRepository, CartMapper cartMapper,
			CartItemRepository cartItemRepository, CartItemMapper cartItemMapper, ProductRepository productRepository) {
		super();
		this.cartRepository = cartRepository;
		this.cartMapper = cartMapper;
		this.cartItemRepository = cartItemRepository;
		this.cartItemMapper = cartItemMapper;
		this.productRepository = productRepository;
	}

	/**
	 * Save.
	 *
	 * @param cartDTO the cart DTO
	 * @return the cart DTO
	 */
	public CartDTO save(CartDTO cartDTO) {
		log.debug("Request to save Cart : {}", cartDTO);
		Cart cart = cartMapper.toEntity(cartDTO);
		return doSaveAndMap(cart);
	}

	private CartDTO doSaveAndMap(Cart cart) {
		cart = cartRepository.save(cart);
		return cartMapper.toDto(cart);
	}

	/**
	 * Find one.
	 *
	 * @param id the id
	 * @return the cart DTO
	 */
	@Transactional(readOnly = true)
	public CartDTO findOne(Long id) {
		log.debug("Request to get Cart : {}", id);
		Cart cart = cartRepository.findOne(id);
		return cartMapper.toDto(cart);
	}
	
	/**
	 * Delete.
	 *
	 * @param id the id
	 */
	public void delete(Long id) {
		log.debug("Request to get Cart : {}", id);
		Cart cart = getCartOrThrow(id);
		// delete all cart item before delete cart
		Iterator<CartItem> iterator = cart.getCartItems().iterator();
		while (iterator.hasNext()) {
			doDeleteCartItem(iterator.next());
			iterator.remove();
		}
		cartRepository.delete(cart);
	}
	
	/**
	 * Adds the item.
	 *
	 * @param cartItemDTO the cart item DTO
	 * @return the cart DTO
	 */
	public CartDTO addItem(CartItemDTO cartItemDTO) {
		log.debug("Request to add cart item : {}", cartItemDTO);
		return updateCartItem(cartItemDTO);
	}

	/**
	 * Update item.
	 *
	 * @param cartItemDTO the cart item DTO
	 * @return the cart DTO
	 */
	public CartDTO updateItem(CartItemDTO cartItemDTO) {
		log.debug("Request to update cart item : {}", cartItemDTO);
		return updateCartItem(cartItemDTO);
	}
	
	private CartDTO updateCartItem(CartItemDTO cartItemDTO) {
		// validate if cart and product correct
		Cart cart = getCartOrThrow(cartItemDTO.getCartId());
		Product product = getProductOrThrow(cartItemDTO.getProduct().getId());
		
		// update product quantity
		CartItem cartItem = cartItemMapper.toEntity(cartItemDTO);
		if(cartItem.getId() == null) { 
			updateProductQuantity(product, cartItem.getQuantity());
		} else {
			CartItem existedCartItem  = getExistedCartItemOrThrow(cart, cartItem.getId());
			Integer requestQuantity =  cartItem.getQuantity() - existedCartItem.getQuantity();
			updateProductQuantity(product, requestQuantity);
		}

		return doUpdate(cart, cartItem, product);
	}

	private void updateProductQuantity(Product product, Integer requestQuantity) {
		if(product.getQuantity() >= requestQuantity) {
			product.setQuantity(product.getQuantity() - requestQuantity);
		} else {
			throw new NotEnoughProductQuantityException();
		}
	}

	private CartItem getExistedCartItemOrThrow(Cart cart, Long cartItemId) {
		for(CartItem cartItemEl : cart.getCartItems()) {
			if(cartItemEl.getId().equals(cartItemId)) {
				return cartItemEl;
			}
		}
		
		throw new CartItemNotExistException();
	}

	private CartDTO doUpdate(Cart cart, CartItem cartItem, Product product) {
		cartItem.setProduct(product);
		cart.addCartItem(cartItem);
		productRepository.save(product);
	    cartItem = cartItemRepository.save(cartItem);
	    return doSaveAndMap(cart);
	}

	/**
	 * Delete item.
	 *
	 * @param id the id
	 * @return the cart DTO
	 */
	public CartDTO deleteItem(Long id) {
		CartItem cartItem = getCartItemOrThrow(id);
		Cart cart = cartItem.getCart();
		cart.removeCartItem(cartItem);
		
		doDeleteCartItem(cartItem);

	    return doSaveAndMap(cart);
	}

	private void doDeleteCartItem(CartItem cartItem) {
		Product product = cartItem.getProduct();
		product.setQuantity(product.getQuantity() + cartItem.getQuantity());
		productRepository.save(product);
	    cartItemRepository.delete(cartItem);
	}

	private Cart getCartOrThrow(Long id) {
		Cart cart = cartRepository.findOne(id);
		if(cart == null) {
			throw new CartNotExistException();
		}
		return cart;
	}

	private CartItem getCartItemOrThrow(Long id) {
		CartItem cartItem = cartItemRepository.getOne(id);
		if(cartItem == null) {
			throw new CartItemNotExistException();
		}
		return cartItem;
	}

	private Product getProductOrThrow(Long productId) {
		Product product = productRepository.findOneByIdAndVisible(productId, true);
		if(product == null) {
			throw new ProductNotExistException();
		}
		return product;
	}

}
