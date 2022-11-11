package com.ll.ebookmarket.app.product.service;

import com.ll.ebookmarket.app.base.exception.DataNotFoundException;
import com.ll.ebookmarket.app.keyword.entity.Keyword;
import com.ll.ebookmarket.app.member.entity.Member;
import com.ll.ebookmarket.app.post.entity.Post;
import com.ll.ebookmarket.app.product.entity.Product;
import com.ll.ebookmarket.app.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ProductService {

    public final ProductRepository productRepository;

    public Page<Product> getList(int page) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
        return this.productRepository.findAll(pageable);
    }

    public Product getProductById(Long id) {
        Optional<Product> product = this.productRepository.findById(id);
        if (product.isPresent()) {
            return product.get();
        } else {
            throw new DataNotFoundException("Post not found");
        }
    }

    public void write(Member author, String subject, Integer price, Keyword keyword) {

        Product product = Product
                .builder()
                .author(author)
                .subject(subject)
                .price(price)
                .keyword(keyword)
                .build();

        this.productRepository.save(product);
    }

    public void modify(Product product, String subject, Integer price, Keyword keyword) {
        product.setSubject(subject);
        product.setPrice(price);
        product.setKeyword(keyword);
        this.productRepository.save(product);
    }

    public void delete(Product product) {
        this.productRepository.delete(product);
    }


}
