package com.ll.ebookmarket.app.cart.controller;

import com.ll.ebookmarket.app.base.dto.RsData;
import com.ll.ebookmarket.app.base.rq.Rq;
import com.ll.ebookmarket.app.cart.entity.CartItem;
import com.ll.ebookmarket.app.cart.service.CartService;
import com.ll.ebookmarket.app.member.entity.Member;
import com.ll.ebookmarket.app.myBook.service.MyBookService;
import com.ll.ebookmarket.app.product.entity.Product;
import com.ll.ebookmarket.app.product.service.ProductService;
import com.ll.ebookmarket.app.security.dto.MemberContext;
import com.ll.ebookmarket.util.Ut;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/cart")
public class CartController {
    private final CartService cartService;
    private final ProductService productService;
    private final MyBookService myBookService;
    private final Rq rq;

    @GetMapping("/list")
    @PreAuthorize("isAuthenticated()")
    public String showItems(@AuthenticationPrincipal MemberContext memberContext, Model model) {
        Member buyer = memberContext.getMember();

        List<CartItem> items = cartService.getItemsByBuyer(buyer);

        model.addAttribute("items", items);

        return "cart/list";
    }

    @GetMapping ("/add/{id}")
    @PreAuthorize("isAuthenticated()")
    public String addItem(@AuthenticationPrincipal MemberContext memberContext, @PathVariable Long id) {
        Member buyer = memberContext.getMember();

        Optional<Product> product = productService.findById(id);
        if(product.isEmpty()){
            return rq.historyBack("존재하지 않는 상품입니다.");
        }

        RsData rsData = cartService.addItem(buyer, product.get());
        if(rsData.isFail()){
            return Rq.redirectWithErrorMsg("/product/list", rsData);
        }

        return Rq.redirectWithMsg("/product/list", rsData);
    }

    @PostMapping("/remove")
    @PreAuthorize("isAuthenticated()")
    public String removeItems(@AuthenticationPrincipal MemberContext memberContext, String ids) {
        Member buyer = memberContext.getMember();

        String[] idsArr = ids.split(",");

        Arrays.stream(idsArr)
                .mapToLong(Long::parseLong)
                .forEach(id -> {
                    CartItem cartItem = cartService.findItemById(id).orElse(null);

                    if (cartService.actorCanDelete(buyer, cartItem)) {
                        cartService.removeItem(cartItem);
                    }
                });

        return "redirect:/cart/list?msg=" + Ut.url.encode("%d건의 품목을 삭제하였습니다.".formatted(idsArr.length));
    }
}
