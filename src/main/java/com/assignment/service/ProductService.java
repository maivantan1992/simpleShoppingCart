package com.assignment.service;

import com.assignment.domain.Product;
import com.assignment.repository.ProductRepository;
import com.assignment.service.dto.ProductDTO;
import com.assignment.service.mapper.ProductMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * Service Implementation for managing Product.
 */
@Service
@Transactional
public class ProductService {

    /** The log. */
    private final Logger log = LoggerFactory.getLogger(ProductService.class);

    /** The product repository. */
    private final ProductRepository productRepository;

    /** The product mapper. */
    private final ProductMapper productMapper;

    /**
     * Instantiates a new product service.
     *
     * @param productRepository the product repository
     * @param productMapper the product mapper
     */
    public ProductService(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    /**
     * Save a product.
     *
     * @param productDTO the entity to save
     * @return the persisted entity
     */
    public ProductDTO save(ProductDTO productDTO) {
        log.debug("Request to save Product : {}", productDTO);
        Product product = productMapper.toEntity(productDTO);
        product = productRepository.save(product);
        return productMapper.toDto(product);
    }

    /**
     * Get all the products.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<ProductDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Products");
        return productRepository.findAll(pageable)
            .map(productMapper::toDto);
    }

    /**
     * Get one product by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Transactional(readOnly = true)
    public ProductDTO findOne(Long id) {
        log.debug("Request to get Product : {}", id);
        Product product = productRepository.findOne(id);
        return productMapper.toDto(product);
    }

    /**
     * Delete the product by id.
     *
     * @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Product : {}", id);
        productRepository.delete(id);
    }

	/**
	 * Search for user.
	 *
	 * @param keyword the keyword
	 * @param pageable the pageable
	 * @return the page
	 */
	public Page<ProductDTO> searchForUser(String keyword, Pageable pageable) {
		log.debug("Request to search product for user");
        return productRepository.searchForUser(keyword, pageable)
            .map(productMapper::toDto);
	}

	/**
	 * Search for admin.
	 *
	 * @param keyword the keyword
	 * @param pageable the pageable
	 * @return the page
	 */
	public Page<ProductDTO> searchForAdmin(String keyword, Pageable pageable) {
		log.debug("Request to search product for admin");
        return productRepository.searchForAdmin(keyword, pageable)
            .map(productMapper::toDto);
	}
}
