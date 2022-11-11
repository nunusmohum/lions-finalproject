package com.ll.ebookmarket.app.order.service;

import com.ll.ebookmarket.app.cart.entity.CartItem;
import com.ll.ebookmarket.app.cart.service.CartService;
import com.ll.ebookmarket.app.member.entity.Member;
import com.ll.ebookmarket.app.member.service.MemberService;
import com.ll.ebookmarket.app.myBook.service.MyBookService;
import com.ll.ebookmarket.app.order.entity.Order;
import com.ll.ebookmarket.app.order.entity.OrderItem;
import com.ll.ebookmarket.app.order.repository.OrderItemRepository;
import com.ll.ebookmarket.app.order.repository.OrderRepository;
import com.ll.ebookmarket.app.product.entity.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {
    private final MemberService memberService;
    private final CartService cartService;
    private final MyBookService myBookService;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    public Optional<Order> findById(long id) {
        return orderRepository.findById(id);
    }

    public List<Order> findAll(Member buyer) {
        return orderRepository.findAllByBuyer(buyer);
    }

    @Transactional
    public Order createFromCart(Member buyer) {
        List<CartItem> cartItems = cartService.getItemsByBuyer(buyer);

        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();

            if (product.isOrderable()) {
                orderItems.add(new OrderItem(product));
            }

            cartService.removeItem(cartItem);
        }

        return create(buyer, orderItems);
    }

    @Transactional
    public Order create(Member buyer, List<OrderItem> orderItems) {
        Order order = Order
                .builder()
                .buyer(buyer)
                .readyStatus("준비")
                .build();

        for (OrderItem orderItem : orderItems) {
            order.addOrderItem(orderItem);
        }

        // 주문 품목으로 부터 이름을 만든다.
        order.makeName();

        orderRepository.save(order);

        return order;
    }

    @Transactional
    public void cancel(Order order) {
        order.setCancelDone();
        orderRepository.save(order);
    }

    @Transactional
    public void refund(Order order) {
        int payPrice = order.getPayPrice();
        memberService.addCash(order.getBuyer(), payPrice, "주문__%d__환불__예치금".formatted(order.getId()));

        order.setRefundDone();
        List<Product> products = order.getOrderItems().stream().map(OrderItem::getProduct).toList();
        myBookService.deleteProducts(order.getBuyer(), products);

        orderRepository.save(order);
    }

    public boolean actorCanSee(Member actor, Order order) {
        return actor.getId().equals(order.getBuyer().getId());
    }

    @Transactional
    public void payByRestCashOnly(Order order) {
        Member buyer = order.getBuyer();

        long restCash = buyer.getRestCash();

        int payPrice = order.calculatePayPrice();

        if (payPrice > restCash) {
            throw new RuntimeException("예치금이 부족합니다.");
        }

        memberService.addCash(buyer, payPrice * -1, "주문__%d__사용__예치금".formatted(order.getId()));

        order.setPaymentDone();

        List<Product> products = order.getOrderItems().stream().map(OrderItem::getProduct).toList();
        myBookService.saveProducts(buyer, products);

        orderRepository.save(order);
    }

    @Transactional
    public void payByTossPayments(Order order, long useRestCash) {
        Member buyer = order.getBuyer();
        int payPrice = order.calculatePayPrice();

        long pgPayPrice = payPrice - useRestCash;
        memberService.addCash(buyer, pgPayPrice, "주문__%d__충전__토스페이먼츠".formatted(order.getId()));
        memberService.addCash(buyer, pgPayPrice * -1, "주문__%d__사용__토스페이먼츠".formatted(order.getId()));

        if ( useRestCash > 0 ) {
            memberService.addCash(buyer, useRestCash * -1, "주문__%d__사용__예치금".formatted(order.getId()));
        }

        order.setPaymentDone();

        List<Product> products = order.getOrderItems().stream().map(OrderItem::getProduct).toList();
        myBookService.saveProducts(buyer, products);

        orderRepository.save(order);
    }

    public boolean actorCanPayment(Member actor, Order order) {
        return actorCanSee(actor, order);
    }

    public List<OrderItem> findAllByPayDateBetweenOrderByIdAsc(LocalDateTime fromDate, LocalDateTime toDate) {
        return orderItemRepository.findAllByPayDateBetween(fromDate, toDate);
    }
}
