package com.ll.ebookmarket.app.home.controller;

import com.ll.ebookmarket.app.post.entity.Post;
import com.ll.ebookmarket.app.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final PostService postService;

    @GetMapping("/")
    public String showMain(Model model) {
        List<Post> postList = postService.getListDesc100();
        this.postService.loadForPrintData(postList);

        model.addAttribute("postList", postList);
        return "home/main";
    }
}
