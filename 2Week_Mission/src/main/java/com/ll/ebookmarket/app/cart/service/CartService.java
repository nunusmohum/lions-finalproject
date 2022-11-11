package com.ll.ebookmarket.app.cart.service;

import com.ll.ebookmarket.app.base.dto.RsData;
import com.ll.ebookmarket.app.cart.entity.CartItem;
import com.ll.ebookmarket.app.cart.repository.CartItemRepository;
import com.ll.ebookmarket.app.member.entity.Member;
import com.ll.ebookmarket.app.myBook.service.MyBookService;
import com.ll.ebookmarket.app.product.entity.Product;
import com.ll.ebookmarket.util.Ut;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CartService {
    private final CartItemRepository cartItemRepository;
    private final MyBookService myBookService;

    public boolean hasItem(Member buyer, Product product) {
        return cartItemRepository.existsByBuyerIdAndProductId(buyer.getId(), product.getId());
    }

    public List<CartItem> getItemsByBuyer(Member buyer) {
        return cartItemRepository.findAllByBuyerId(buyer.getId());
    }

    public Optional<CartItem> findItemById(long id) {
        return cartItemRepository.findById(id);
    }

    @Transactional
    public RsData addItem(Member buyer, Product product) {
        List<CartItem> items = getItemsByBuyer(buyer);
        List<Product> products = myBookService.getProductsByMember(buyer);

        if(products.contains(product)){
            return RsData.of("F-1", "이미 구매한 상품입니다.");
        }

        if(product.getAuthor().equals(buyer)){
            return RsData.of("F-1", "자신의 도서는 장바구니에 담을 수 없습니다.");
        }

        if(items.stream().anyMatch(cartItem -> cartItem.getProduct().equals(product))){
            return RsData.of("F-1", "이미 추가된 상품입니다.");
        }

        CartItem cartItem = CartItem.builder()
                .buyer(buyer)
                .product(product)
                .build();

        cartItemRepository.save(cartItem);

        return RsData.of("S-1", "장바구니에 상품을 추가하였습니다.");
    }

    @Transactional
    public void removeItem(CartItem cartItem) {
        cartItemRepository.delete(cartItem);
    }

    @Transactional
    public boolean removeItem(Member buyer, Product product) {
        CartItem oldCartItem = cartItemRepository.findByBuyerIdAndProductId(buyer.getId(), product.getId()).orElse(null);

        if (oldCartItem != null) {
            cartItemRepository.delete(oldCartItem);
            return true;
        }

        return false;
    }

    public boolean actorCanDelete(Member buyer, CartItem cartItem) {
        return buyer.getId().equals(cartItem.getBuyer().getId());
    }
}
