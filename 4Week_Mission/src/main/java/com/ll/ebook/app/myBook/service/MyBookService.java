package com.ll.ebook.app.myBook.service;

import com.ll.ebook.app.member.entity.Member;
import com.ll.ebook.app.myBook.entity.MyBook;
import com.ll.ebook.app.base.dto.RsData;
import com.ll.ebook.app.myBook.repository.MyBookRepository;
import com.ll.ebook.app.order.entity.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MyBookService {
    private final MyBookRepository myBookRepository;

    public List<MyBook> getMyBooksByMember(Member member) {
        return myBookRepository.findAllByOwnerId(member.getId());
    }

    public Optional<MyBook> getMyBookById(Long myBookId) {
        return myBookRepository.findById(myBookId);
    }

    @Transactional
    public RsData add(Order order) {
        order.getOrderItems()
                .stream()
                .map(orderItem -> MyBook.builder()
                        .owner(order.getBuyer())
                        .orderItem(orderItem)
                        .product(orderItem.getProduct())
                        .build())
                .forEach(myBookRepository::save);

        return RsData.of("S-1", "나의 책장에 추가되었습니다.");
    }

    @Transactional
    public RsData remove(Order order) {
        order.getOrderItems()
                .stream()
                .forEach(orderItem -> myBookRepository.deleteByProductIdAndOwnerId(orderItem.getProduct().getId(), order.getBuyer().getId()));

        return RsData.of("S-1", "나의 책장에서 제거되었습니다.");
    }
}
