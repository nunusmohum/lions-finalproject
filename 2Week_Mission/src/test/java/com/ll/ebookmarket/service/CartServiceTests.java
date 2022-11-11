package com.ll.ebookmarket.service;

import com.ll.ebookmarket.app.base.dto.RsData;
import com.ll.ebookmarket.app.cart.service.CartService;
import com.ll.ebookmarket.app.member.entity.Member;
import com.ll.ebookmarket.app.member.service.MemberService;
import com.ll.ebookmarket.app.myBook.service.MyBookService;
import com.ll.ebookmarket.app.product.entity.Product;
import com.ll.ebookmarket.app.product.service.ProductService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class CartServiceTests {
    @Autowired
    private CartService cartService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private ProductService productService;

    @Autowired
    private MyBookService myBookService;

    @Test
    @DisplayName("장바구니에 담을 수 있어야 한다.")
    void t1() {
        //given
        Member buyer = memberService.findByUsername("user1").get();
        Product product = productService.findById(2).get();

        //when
        RsData rsData = cartService.addItem(buyer, product);

        //then
        Assertions.assertThat(rsData.isSuccess()).isTrue();
    }

    @Test
    @DisplayName("자신의 상품은 장바구니에 담을 수 없다.")
    void t2() {
        //given
        Member buyer = memberService.findByUsername("user1").get();
        Product product = productService.findById(1).get();

        //when
        RsData rsData = cartService.addItem(buyer, product);

        //then
        Assertions.assertThat(rsData.isFail()).isTrue();
    }

    @Test
    @DisplayName("장바구니에서 제거할 수 있다.")
    void t3() {
        //given
        Member buyer = memberService.findByUsername("user1").get();
        Product product = productService.findById(2).get();
        RsData rsData = cartService.addItem(buyer, product);

        //when
        cartService.removeItem(buyer, product);

        //then
        Assertions.assertThat(cartService.hasItem(buyer, product)).isFalse();
    }

    @Test
    @DisplayName("이미 구매한 상품은 장바구니에 담을 수 없다.")
    void t4() {
        Member buyer = memberService.findByUsername("user1").get();
        Product product = productService.findById(2).get();
        myBookService.saveProduct(buyer, product);

        //when
        RsData rsData = cartService.addItem(buyer, product);

        //then
        Assertions.assertThat(rsData.isFail()).isTrue();
    }

}
