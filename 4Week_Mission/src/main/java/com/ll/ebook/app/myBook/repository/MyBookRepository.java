package com.ll.ebook.app.myBook.repository;

import com.ll.ebook.app.myBook.entity.MyBook;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MyBookRepository extends JpaRepository<MyBook, Long> {
    List<MyBook> findAllByOwnerId(long memberId);
    void deleteByProductIdAndOwnerId(long productId, long ownerId);
}
