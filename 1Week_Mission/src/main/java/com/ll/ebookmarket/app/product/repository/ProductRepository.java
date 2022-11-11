package com.ll.ebookmarket.app.product.repository;

import com.ll.ebookmarket.app.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
