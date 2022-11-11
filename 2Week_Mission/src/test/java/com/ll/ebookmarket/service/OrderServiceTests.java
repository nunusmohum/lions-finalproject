package com.ll.ebookmarket.service;

import com.ll.ebookmarket.app.base.dto.RsData;
import com.ll.ebookmarket.app.cart.service.CartService;
import com.ll.ebookmarket.app.member.entity.Member;
import com.ll.ebookmarket.app.member.service.MemberService;
import com.ll.ebookmarket.app.myBook.service.MyBookService;
import com.ll.ebookmarket.app.order.entity.Order;
import com.ll.ebookmarket.app.order.service.OrderService;
import com.ll.ebookmarket.app.product.entity.Product;
import com.ll.ebookmarket.app.product.service.ProductService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class OrderServiceTests {
    @Autowired
    private CartService cartService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private ProductService productService;

    @Autowired
    private MyBookService myBookService;

    @Autowired
    private OrderService orderService;

    @Test
    @DisplayName("주문 생성이 가능하다.")
    void t1() {
        //given
        Member buyer = memberService.findByUsername("user1").get();
        Product product = productService.findById(2).get();
        RsData rsData = cartService.addItem(buyer, product);

        //when
        Order order = orderService.createFromCart(buyer);

        //then
        Assertions.assertThat(order).isNotNull();
    }

    @Test
    @DisplayName("주문 취소가 가능하다.")
    void t2() {
        //given
        Member buyer = memberService.findByUsername("user1").get();
        Product product = productService.findById(2).get();
        RsData rsData = cartService.addItem(buyer, product);
        Order order = orderService.createFromCart(buyer);

        //when
        orderService.cancel(order);

        //then
        Assertions.assertThat(order.isCanceled()).isTrue();
    }

    @Test
    @DisplayName("환불이 가능하다.")
    void t3() {
        //given
        Member buyer = memberService.findByUsername("user1").get();
        Product product = productService.findById(2).get();
        RsData rsData = cartService.addItem(buyer, product);
        Order order = orderService.createFromCart(buyer);
        order.setPaymentDone();

        //when
        orderService.refund(order);

        //then
        Assertions.assertThat(order.isRefunded()).isTrue();
    }

    @Test
    @DisplayName("10분이 지나면 환불이 불가능하다.")
    void t4() {
        //given
        Member buyer = memberService.findByUsername("user1").get();
        Product product = productService.findById(2).get();
        RsData rsData = cartService.addItem(buyer, product);
        Order order = orderService.createFromCart(buyer);
        order.setPaymentDone();

        //when

        //then
        Assertions.assertThat(order.isRefundable(LocalDateTime.now().plusSeconds(630))).isFalse();
    }
}
