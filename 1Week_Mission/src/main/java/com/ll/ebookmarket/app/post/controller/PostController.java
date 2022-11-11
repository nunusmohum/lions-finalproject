package com.ll.ebookmarket.app.post.controller;

import com.ll.ebookmarket.app.member.entity.Member;
import com.ll.ebookmarket.app.member.service.MemberService;
import com.ll.ebookmarket.app.post.dto.PostForm;
import com.ll.ebookmarket.app.post.entity.Post;
import com.ll.ebookmarket.app.post.service.PostService;
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
import java.util.List;

@RequestMapping("/post")
@RequiredArgsConstructor
@Controller
public class PostController {

    private final PostService postService;
    private final MemberService memberService;

    @RequestMapping("/list")
    public String list(Model model, @RequestParam(value="page", defaultValue="0") int page) {
        Page<Post> paging = this.postService.getList(page);
        this.postService.loadForPrintData(paging);
        model.addAttribute("paging", paging);

        return "post/postList";
    }

    @RequestMapping(value = "/{id}")
    public String detail(Model model, @PathVariable("id") Long id) {
        Post post = this.postService.getForPrintPostById(id);
        model.addAttribute("post", post);
        return "post/postDetail";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/write")
    public String write(PostForm postForm) {
        return "post/postForm";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/write")
    public String write(@Valid PostForm postForm, BindingResult bindingResult, Principal principal) {
        if (bindingResult.hasErrors()) {
            return "post/postForm";
        }

        Member member = this.memberService.findByUsername(principal.getName());
        this.postService.write(member, postForm.getSubject(), postForm.getContent(), postForm.getContentHtml(), postForm.getHashTagContents());
        return "redirect:/post/list";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}/modify")
    public String modify(Model model, PostForm postForm, @PathVariable("id") Long id, Principal principal) {
        Post post = this.postService.getForPrintPostById(id);
        if(!post.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        postForm.setSubject(post.getSubject());
        postForm.setContent(post.getContent());
        postForm.setHashTagContents(post.getExtra_inputValue_hashTagContents());
        return "post/postForm";
    }
    
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{id}/modify")
    public String modify(@Valid PostForm postForm, BindingResult bindingResult,
                               Principal principal, @PathVariable("id") Long id) {
        if (bindingResult.hasErrors()) {
            return "post/postForm";
        }
        Post post = this.postService.getForPrintPostById(id);
        if (!post.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        this.postService.modify(post, postForm.getSubject(), postForm.getContent(), postForm.getContentHtml(), postForm.getHashTagContents());
        return String.format("redirect:/post/%s", id);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}/delete")
    public String postDelete(Principal principal, @PathVariable("id") Long id) {
        Post post = this.postService.getPostById(id);
        if (!post.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
        }
        this.postService.delete(post);
        return "redirect:/post/list";
    }

    @GetMapping("/search")
    public String search(Model model, Principal principal, @RequestParam("kwType") String kwType, @RequestParam("kw") String kw) {
        List<Post> postList = this.postService.search(kwType, kw);
        this.postService.loadForPrintData(postList);
        model.addAttribute("postList", postList);
        return "post/postSearchList";
    }
}
