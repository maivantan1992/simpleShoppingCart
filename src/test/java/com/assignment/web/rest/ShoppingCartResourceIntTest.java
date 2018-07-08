package com.assignment.web.rest;

import static com.assignment.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import javax.persistence.EntityManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import com.assignment.AssignmentApp;
import com.assignment.domain.Cart;
import com.assignment.domain.CartItem;
import com.assignment.domain.Product;
import com.assignment.repository.CartItemRepository;
import com.assignment.repository.CartRepository;
import com.assignment.service.ShoppingCartService;
import com.assignment.service.dto.CartItemDTO;
import com.assignment.service.mapper.CartItemMapper;
import com.assignment.web.rest.errors.ExceptionTranslator;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AssignmentApp.class)
public class ShoppingCartResourceIntTest {

	private static final Integer DEFAULT_QUANTITY = 1;
	private static final Integer UPDATED_QUANTITY = 2;

	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private CartItemRepository cartItemRepository;

	@Autowired
	private CartItemMapper cartItemMapper;
	
	@Autowired
	private ShoppingCartService shoppingCartService;

	@Autowired
	private MappingJackson2HttpMessageConverter jacksonMessageConverter;

	@Autowired
	private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

	@Autowired
	private ExceptionTranslator exceptionTranslator;

	@Autowired
	private EntityManager em;

	private MockMvc restShoppingCartMockMvc;

	private Cart cart;

	private CartItem cartItem;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		final ShoppingCartResource shoppingCartResource = new ShoppingCartResource(shoppingCartService);
		this.restShoppingCartMockMvc = MockMvcBuilders.standaloneSetup(shoppingCartResource)
				.setCustomArgumentResolvers(pageableArgumentResolver).setControllerAdvice(exceptionTranslator)
				.setConversionService(createFormattingConversionService()).setMessageConverters(jacksonMessageConverter)
				.build();
	}

	/**
	 * Create an entity for this test.
	 *
	 * This is a static method, as tests for other entities might also need it, if
	 * they test an entity which requires the current entity.
	 */
	public static Cart createEntity(EntityManager em) {
		Cart cart = new Cart();
		return cart;
	}

	public CartItem createCartItemEntity(EntityManager em) {
		CartItem cartItem = new CartItem().quantity(DEFAULT_QUANTITY);
		// Add required entity
		Product product = ProductResourceIntTest.createEntity(em);
		em.persist(product);
		em.flush();
		cartItem.setProduct(product);
		// Add required entity
		Cart cart = createEntity(em);
		em.persist(cart);
		em.flush();
		cartItem.setCart(cart);
		return cartItem;
	}

	@Before
	public void initTest() {
		cart = createEntity(em);
		cartItem = createCartItemEntity(em);
	}

	@Test
	@Transactional
	public void initCart() throws Exception {
		int databaseSizeBeforeCreate = cartRepository.findAll().size();

		// Create the Cart
		restShoppingCartMockMvc.perform(post("/api/shopping-carts").contentType(TestUtil.APPLICATION_JSON_UTF8))
				.andExpect(status().isCreated());

		// Validate the Cart in the database
		List<Cart> cartList = cartRepository.findAll();
		assertThat(cartList).hasSize(databaseSizeBeforeCreate + 1);
	}

	@Test
	@Transactional
	public void getCart() throws Exception {
		// Initialize the database
		cartRepository.saveAndFlush(cart);

		// Get the cart
		restShoppingCartMockMvc.perform(get("/api/shopping-carts/{id}", cart.getId())).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andExpect(jsonPath("$.id").value(cart.getId().intValue()));
	}
	
	@Test
	@Transactional
	public void getNonExistingCart() throws Exception {
		// Get the cart
		restShoppingCartMockMvc.perform(get("/api/shopping-carts/{id}", Long.MAX_VALUE)).andExpect(status().isNotFound());
	}

	@Test
	@Transactional
	public void deleteCart() throws Exception {
		int databaseSizeBeforeCreate = cartRepository.findAll().size();
		// Initialize the database
		cartRepository.saveAndFlush(cart);
		
		List<Cart> cartList = cartRepository.findAll();
		assertThat(cartList).hasSize(databaseSizeBeforeCreate + 1);

		// Delete the cart
		restShoppingCartMockMvc.perform(delete("/api/shopping-carts/{id}", cart.getId())).andExpect(status().isOk());
				
		// Validate the Cart in the database
		cartList = cartRepository.findAll();
		assertThat(cartList).hasSize(databaseSizeBeforeCreate);
	}
	
	@Test
    @Transactional
    public void checkQuantityIsRequired() throws Exception {
        int databaseSizeBeforeTest = cartItemRepository.findAll().size();
        // set the field null
        cartItem.setQuantity(null);

        // Create the CartItem
        CartItemDTO cartItemDTO = cartItemMapper.toDto(cartItem);
 		restShoppingCartMockMvc.perform(post("/api//shopping-carts/add-item")
 				.contentType(TestUtil.APPLICATION_JSON_UTF8)
 				.content(TestUtil.convertObjectToJsonBytes(cartItemDTO)))
 				.andExpect(status().isBadRequest());

        List<CartItem> cartItemList = cartItemRepository.findAll();
        assertThat(cartItemList).hasSize(databaseSizeBeforeTest);
    }
	
	@Test
    @Transactional
    public void checkProductIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = cartItemRepository.findAll().size();
        // set the field null
        cartItem.setProduct(null);

        // Create the CartItem
        CartItemDTO cartItemDTO = cartItemMapper.toDto(cartItem);
 		restShoppingCartMockMvc.perform(post("/api//shopping-carts/add-item")
 				.contentType(TestUtil.APPLICATION_JSON_UTF8)
 				.content(TestUtil.convertObjectToJsonBytes(cartItemDTO)))
 				.andExpect(status().isBadRequest());

        List<CartItem> cartItemList = cartItemRepository.findAll();
        assertThat(cartItemList).hasSize(databaseSizeBeforeTest);
    }
	
	@Test
	@Transactional
	public void addCartItemCartNotExist() throws Exception {
		int databaseSizeBeforeCreate = cartItemRepository.findAll().size();
		// Create the CartItem
		CartItemDTO cartItemDTO = cartItemMapper.toDto(cartItem);
		cartItemDTO.setCartId(Long.MAX_VALUE);
		restShoppingCartMockMvc.perform(post("/api/shopping-carts/add-item")
				.contentType(TestUtil.APPLICATION_JSON_UTF8)
				.content(TestUtil.convertObjectToJsonBytes(cartItemDTO)))
				.andExpect(status().isBadRequest());

		// Validate the CartItem in the database
		List<CartItem> cartItemList = cartItemRepository.findAll();
		assertThat(cartItemList).hasSize(databaseSizeBeforeCreate);
	}
	
	@Test
	@Transactional
	public void addCartItem() throws Exception {
		int databaseSizeBeforeCreate = cartItemRepository.findAll().size();
		// Create the CartItem
		CartItemDTO cartItemDTO = cartItemMapper.toDto(cartItem);
		restShoppingCartMockMvc.perform(post("/api/shopping-carts/add-item")
				.contentType(TestUtil.APPLICATION_JSON_UTF8)
				.content(TestUtil.convertObjectToJsonBytes(cartItemDTO)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").value(cartItem.getCart().getId().intValue()))
	            .andExpect(jsonPath("$.cartItems.[*].quantity").value(hasItem(cartItem.getQuantity())))
	            .andExpect(jsonPath("$.cartItems.[*].product.id").value(hasItem(cartItem.getProduct().getId().intValue())));

		// Validate the CartItem in the database
		List<CartItem> cartItemList = cartItemRepository.findAll();
		assertThat(cartItemList).hasSize(databaseSizeBeforeCreate + 1);
		CartItem testCartItem = cartItemList.get(cartItemList.size() - 1);
		assertThat(testCartItem.getQuantity()).isEqualTo(DEFAULT_QUANTITY);
	}
	
	@Test
	@Transactional
	public void addCartItemNotEnoughProductQuantity() throws Exception {
		int databaseSizeBeforeCreate = cartItemRepository.findAll().size();
		// Create the CartItem
		CartItemDTO cartItemDTO = cartItemMapper.toDto(cartItem);
		cartItemDTO.setQuantity(Integer.MAX_VALUE);
		restShoppingCartMockMvc.perform(post("/api/shopping-carts/add-item")
				.contentType(TestUtil.APPLICATION_JSON_UTF8)
				.content(TestUtil.convertObjectToJsonBytes(cartItemDTO)))
				.andExpect(status().isBadRequest());

		// Validate the CartItem in the database
		List<CartItem> cartItemList = cartItemRepository.findAll();
		assertThat(cartItemList).hasSize(databaseSizeBeforeCreate);
	}
	
	@Test
	@Transactional
	public void addCartItemProductNotExist() throws Exception {
		int databaseSizeBeforeCreate = cartItemRepository.findAll().size();
		// Create the CartItem
		CartItemDTO cartItemDTO = cartItemMapper.toDto(cartItem);
		cartItemDTO.getProduct().setId(Long.MAX_VALUE);
		restShoppingCartMockMvc.perform(post("/api/shopping-carts/add-item")
				.contentType(TestUtil.APPLICATION_JSON_UTF8)
				.content(TestUtil.convertObjectToJsonBytes(cartItemDTO)))
				.andExpect(status().isBadRequest());

		// Validate the CartItem in the database
		List<CartItem> cartItemList = cartItemRepository.findAll();
		assertThat(cartItemList).hasSize(databaseSizeBeforeCreate);
	}
	
	@Test
	@Transactional
	public void updateCartItem() throws Exception {
		// Initialize the database
        cartItemRepository.saveAndFlush(cartItem);
        Cart cart = cartItem.getCart();
        cart.addCartItem(cartItem);
        cartRepository.saveAndFlush(cart);
        int databaseSizeBeforeUpdate = cartItemRepository.findAll().size();

        // Update the cartItem
        CartItem updatedCartItem = cartItemRepository.findOne(cartItem.getId());
        CartItemDTO cartItemDTO = cartItemMapper.toDto(updatedCartItem);
        cartItemDTO.setQuantity(UPDATED_QUANTITY);
        
        restShoppingCartMockMvc.perform(put("/api/shopping-carts/update-item")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(cartItemDTO)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(cartItem.getCart().getId().intValue()))
			.andExpect(jsonPath("$.cartItems.[*].id").value(hasItem(cartItem.getId().intValue())))
	        .andExpect(jsonPath("$.cartItems.[*].quantity").value(hasItem(UPDATED_QUANTITY)))
	        .andExpect(jsonPath("$.cartItems.[*].product.id").value(hasItem(cartItem.getProduct().getId().intValue())));

        // Validate the CartItem in the database
        List<CartItem> cartItemList = cartItemRepository.findAll();
        assertThat(cartItemList).hasSize(databaseSizeBeforeUpdate);
        CartItem testCartItem = cartItemList.get(cartItemList.size() - 1);
        assertThat(testCartItem.getQuantity()).isEqualTo(UPDATED_QUANTITY);
	}
	
	@Test
	@Transactional
	public void updateCartItemCartNotExist() throws Exception {
		// Initialize the database
        cartItemRepository.saveAndFlush(cartItem);
        Cart cart = cartItem.getCart();
        cart.addCartItem(cartItem);
        cartRepository.saveAndFlush(cart);
        int databaseSizeBeforeUpdate = cartItemRepository.findAll().size();

        // Update the cartItem
        CartItem updatedCartItem = cartItemRepository.findOne(cartItem.getId());
        CartItemDTO cartItemDTO = cartItemMapper.toDto(updatedCartItem);
        cartItemDTO.setCartId(Long.MAX_VALUE);
        
        restShoppingCartMockMvc.perform(put("/api/shopping-carts/update-item")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(cartItemDTO)))
			.andExpect(status().isBadRequest());

        // Validate the CartItem in the database
        List<CartItem> cartItemList = cartItemRepository.findAll();
        assertThat(cartItemList).hasSize(databaseSizeBeforeUpdate);
        CartItem testCartItem = cartItemList.get(cartItemList.size() - 1);
        assertThat(testCartItem.getQuantity()).isEqualTo(DEFAULT_QUANTITY);
	}
	
	@Test
	@Transactional
	public void updateCartItemNotEnoughProductQuantity() throws Exception {
		// Initialize the database
        cartItemRepository.saveAndFlush(cartItem);
        Cart cart = cartItem.getCart();
        cart.addCartItem(cartItem);
        cartRepository.saveAndFlush(cart);
        int databaseSizeBeforeUpdate = cartItemRepository.findAll().size();

        // Update the cartItem
        CartItem updatedCartItem = cartItemRepository.findOne(cartItem.getId());
        CartItemDTO cartItemDTO = cartItemMapper.toDto(updatedCartItem);
        cartItemDTO.setQuantity(Integer.MAX_VALUE);
        
        restShoppingCartMockMvc.perform(put("/api/shopping-carts/update-item")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(cartItemDTO)))
			.andExpect(status().isBadRequest());

        // Validate the CartItem in the database
        List<CartItem> cartItemList = cartItemRepository.findAll();
        assertThat(cartItemList).hasSize(databaseSizeBeforeUpdate);
        CartItem testCartItem = cartItemList.get(cartItemList.size() - 1);
        assertThat(testCartItem.getQuantity()).isEqualTo(DEFAULT_QUANTITY);
	}
	
	@Test
	@Transactional
	public void updateCartItemProductNotExist() throws Exception {
		// Initialize the database
        cartItemRepository.saveAndFlush(cartItem);
        Cart cart = cartItem.getCart();
        cart.addCartItem(cartItem);
        cartRepository.saveAndFlush(cart);
        int databaseSizeBeforeUpdate = cartItemRepository.findAll().size();

        // Update the cartItem
        CartItem updatedCartItem = cartItemRepository.findOne(cartItem.getId());
        CartItemDTO cartItemDTO = cartItemMapper.toDto(updatedCartItem);
        cartItemDTO.getProduct().setId(Long.MAX_VALUE);
        
        restShoppingCartMockMvc.perform(put("/api/shopping-carts/update-item")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(cartItemDTO)))
			.andExpect(status().isBadRequest());

        // Validate the CartItem in the database
        List<CartItem> cartItemList = cartItemRepository.findAll();
        assertThat(cartItemList).hasSize(databaseSizeBeforeUpdate);
        CartItem testCartItem = cartItemList.get(cartItemList.size() - 1);
        assertThat(testCartItem.getQuantity()).isEqualTo(DEFAULT_QUANTITY);
	}
	
	@Test
	@Transactional
	public void deleteCartItem() throws Exception {
		// Initialize the database
        cartItemRepository.saveAndFlush(cartItem);
        Cart cart = cartItem.getCart();
        cart.addCartItem(cartItem);
        cartRepository.saveAndFlush(cart);
        int databaseSizeBeforeDelete = cartItemRepository.findAll().size();
        
        Long cartItemId = cartItem.getId();
        
        restShoppingCartMockMvc.perform(delete("/api/shopping-carts/delete-item/{id}", cartItemId))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(cart.getId().intValue()))
			.andExpect(jsonPath("$.cartItems.[*].id").value(not(hasItem(cartItemId))));

        // Validate the CartItem in the database
        List<CartItem> cartItemList = cartItemRepository.findAll();
        assertThat(cartItemList).hasSize(databaseSizeBeforeDelete - 1);
	}
	
}
