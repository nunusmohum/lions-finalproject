package com.ll.ebookmarket.app.myBook.repository;

import com.ll.ebookmarket.app.myBook.entity.MyBook;
import com.ll.ebookmarket.app.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MyBookRepository extends JpaRepository<MyBook, Long> {
    List<MyBook> findAllByMemberId(long memberId);

    List<MyBook> findAllByMemberIdAndProductId(Long memberId, Long productId);
}
