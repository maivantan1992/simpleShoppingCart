package com.assignment.repository;

import com.assignment.domain.Product;

import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the Product entity.
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
	Product findOneByIdAndVisible(Long id, Boolean visible);

	@Query("select p from Product p where p.visible = true and p.name like %?1%")
	Page<Product> searchForUser(String keyword, Pageable pageable);

	@Query("select p from Product p where p.name like %?1%")
	Page<Product> searchForAdmin(String keyword, Pageable pageable);
}
