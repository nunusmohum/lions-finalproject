package com.ll.ebookmarket.app.myBook.service;

import com.ll.ebookmarket.app.member.entity.Member;
import com.ll.ebookmarket.app.myBook.entity.MyBook;
import com.ll.ebookmarket.app.myBook.repository.MyBookRepository;
import com.ll.ebookmarket.app.product.entity.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MyBookService {
    private final MyBookRepository myBookRepository;

    public List<Product> getProductsByMember(Member member) {
        List<MyBook> myBooks = myBookRepository.findAllByMemberId(member.getId());
        return myBooks.stream().map(MyBook::getProduct).toList();
    }

    public void saveProducts(Member member, List<Product> products) {
        products.forEach(product -> saveProduct(member, product));
    }

    public void saveProduct(Member member, Product product) {
        MyBook myBook = MyBook.builder()
                .member(member)
                .product(product)
                .build();

        myBookRepository.save(myBook);
    }

    public void deleteProducts(Member member, List<Product> products) {
        products.forEach(product -> deleteProduct(member, product));
    }

    public void deleteProduct(Member member, Product product) {
        List<MyBook> myBooks = myBookRepository.findAllByMemberIdAndProductId(member.getId(), product.getId());
        myBookRepository.deleteAll(myBooks);
    }
}
