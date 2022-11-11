package com.ll.ebookmarket.app.myBook.controller;

import com.ll.ebookmarket.app.cart.entity.CartItem;
import com.ll.ebookmarket.app.member.entity.Member;
import com.ll.ebookmarket.app.myBook.service.MyBookService;
import com.ll.ebookmarket.app.product.entity.Product;
import com.ll.ebookmarket.app.security.dto.MemberContext;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/mybook")
public class MyBookController {

    private final MyBookService myBookService;

    @GetMapping("/list")
    @PreAuthorize("isAuthenticated()")
    public String showItems(@AuthenticationPrincipal MemberContext memberContext, Model model) {
        Member buyer = memberContext.getMember();

        List<Product> products = myBookService.getProductsByMember(buyer);

        model.addAttribute("products", products);

        return "mybook/list";
    }
}
