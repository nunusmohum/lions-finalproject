package com.ll.ebookmarket.app.order.repository;

import com.ll.ebookmarket.app.member.entity.Member;
import com.ll.ebookmarket.app.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findAllByBuyer(Member buyer);
}
