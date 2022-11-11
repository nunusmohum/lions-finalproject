package com.ll.ebook.app.withdraw.repository;

import com.ll.ebook.app.product.entity.Product;
import com.ll.ebook.app.withdraw.entity.Withdraw;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface WithdrawRepository extends JpaRepository<Withdraw, Long> {
    List<Withdraw> findAllByOrderByIdDesc();
}
