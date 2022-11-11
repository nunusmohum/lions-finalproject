package com.ll.ebookmarket.app.product.controller;

import com.ll.ebookmarket.app.hashtag.entity.HashTag;
import com.ll.ebookmarket.app.keyword.entity.Keyword;
import com.ll.ebookmarket.app.keyword.service.KeywordService;
import com.ll.ebookmarket.app.member.entity.Member;
import com.ll.ebookmarket.app.member.service.MemberService;
import com.ll.ebookmarket.app.post.entity.Post;
import com.ll.ebookmarket.app.post.service.PostService;
import com.ll.ebookmarket.app.product.dto.ProductForm;
import com.ll.ebookmarket.app.product.entity.Product;
import com.ll.ebookmarket.app.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.security.Principal;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequestMapping("/product")
@RequiredArgsConstructor
@Controller
public class ProductController {

    private final ProductService productService;
    private final PostService postService;
    private final MemberService memberService;
    private final KeywordService keywordService;

    @RequestMapping("/list")
    public String list(Model model, @RequestParam(value="page", defaultValue="0") int page) {
        Page<Product> paging = this.productService.getList(page);

        model.addAttribute("paging", paging);

        return "product/productList";
    }

    @RequestMapping(value = "/{id}")
    public String detail(Model model, @PathVariable("id") Long id) {
        Product product = this.productService.getProductById(id);

        model.addAttribute("product", product);

        return "product/productDetail";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/write")
    public String productWrite(Model model, ProductForm productForm, Principal principal) {
        Member member = memberService.findByUsername(principal.getName());
        List<Post> myPostList = postService.findMyAllList(member);
        List<Keyword> keywordList = myPostList.stream()
                .map(Post::getHashtagList)
                .flatMap(Collection::stream)
                .map(HashTag::getKeyword)
                .distinct().toList();

        model.addAttribute("keywordList", keywordList);
        return "product/productForm";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/write")
    public String productWrite(@Valid ProductForm productForm, BindingResult bindingResult, Principal principal) {
        if (bindingResult.hasErrors()) {
            return "product/productForm";
        }

        Member member = this.memberService.findByUsername(principal.getName());
        Keyword keyword = this.keywordService.findByContent(productForm.getKeywordContent());

        this.productService.write(member, productForm.getSubject(), Integer.parseInt(productForm.getPrice()), keyword);
        return "redirect:/product/list";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}/modify")
    public String productModify(ProductForm productForm, @PathVariable("id") Long id, Principal principal) {
        Product product = this.productService.getProductById(id);

        if(!product.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }

        productForm.setSubject(product.getSubject());
        productForm.setPrice(product.getPrice().toString());

        return "product/productForm";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{id}/modify")
    public String productModify(@Valid ProductForm productForm, BindingResult bindingResult,
                             Principal principal, @PathVariable("id") Long id) {
        if (bindingResult.hasErrors()) {
            return "product/productForm";
        }

        Product product = this.productService.getProductById(id);
        Keyword keyword = this.keywordService.findByContent(productForm.getKeywordContent());

        if (!product.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        this.productService.modify(product, productForm.getSubject(), Integer.parseInt(productForm.getPrice()), keyword);

        return String.format("redirect:/product/%s", id);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}/delete")
    public String productDelete(Principal principal, @PathVariable("id") Long id) {
        Product product = this.productService.getProductById(id);

        if (!product.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
        }

        this.productService.delete(product);

        return "redirect:/product/list";
    }
}
