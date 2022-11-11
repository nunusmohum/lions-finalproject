package com.ll.ebookmarket.app.base.initData;

import com.ll.ebookmarket.app.member.entity.Member;
import com.ll.ebookmarket.app.member.service.MemberService;
import com.ll.ebookmarket.app.post.service.PostService;
import com.ll.ebookmarket.app.product.entity.Product;
import com.ll.ebookmarket.app.product.service.ProductService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile({"dev", "test"})
public class NotProdInitData {
    private boolean initDataDone = false;

    @Bean
    CommandLineRunner initData(
            MemberService memberService,
            PostService postService,
            ProductService productService
    ) {
        return args -> {
            if (initDataDone) {
                return;
            }

            initDataDone = true;

            Member member1 = memberService.join("user1", "1234", "user1@test.com", null);
            Member member2 = memberService.join("user2", "1234", "user2@test.com", "홍길순");

            postService.write(
                    member1,
                    "자바를 우아하게 사용하는 방법",
                    "# 내용 1",
                    "<h1>내용 1</h1>",
                    "#IT #자바 #카프카"
            );

            postService.write(
                    member1,
                    "자바스크립트를 우아하게 사용하는 방법",
                    """
                            # 자바스크립트는 이렇게 쓰세요.
                                                    
                            ```js
                            const a = 10;
                            console.log(a);
                            ```
                            """.stripIndent(),
                    """
                            <h1>자바스크립트는 이렇게 쓰세요.</h1><div data-language="js" class="toastui-editor-ww-code-block-highlighting"><pre class="language-js"><code data-language="js" class="language-js"><span class="token keyword">const</span> a <span class="token operator">=</span> <span class="token number">10</span><span class="token punctuation">;</span>
                            <span class="token console class-name">console</span><span class="token punctuation">.</span><span class="token method function property-access">log</span><span class="token punctuation">(</span>a<span class="token punctuation">)</span><span class="token punctuation">;</span></code></pre></div>
                                                    """.stripIndent(),
                    "#IT #프론트엔드 #리액트"
            );

            postService.write(member2, "프론트 기초", "태그는 열고 닫아야 해요", "태그는 열고 닫아야 해요", "#IT# 프론트엔드 #HTML #CSS");
            postService.write(member2, "스프링 기초", "스프링 MVC 패턴은 다음과 같아요", "스프링 MVC 패턴은 다음과 같아요", "#IT #스프링부트 #자바");
            postService.write(member1, "카프카 정리", "프로듀서와 컨슈머 차이는...", "프로듀서와 컨슈머 차이는...", "#IT #자바 #카프카");
            postService.write(member1, "리액트 기초", "리액트 훅은 다음과 같아요", "리액트 훅은 다음과 같아요", "#IT #프론트엔드 #REACT");
            postService.write(member2, "프론트 기초2", "css파일 분리하는 방법이에요", "css파일 분리하는 방법이에요", "#IT# 프론트엔드 #HTML #CSS");
            postService.write(member2, "스프링 기초2", "디펜던시 인젝션이란...", "디펜던시 인젝션이란...", "#IT #스프링부트 #자바");

            Product product1 = productService.create(member1, "카프카의 모든 것", 30_000, "카프카", "#IT #카프카");
            Product product2 = productService.create(member2, "스프링 한눈에 보기", 40_000, "스프링부트", "#IT #자바");
            Product product3 = productService.create(member1, "따라 치며 배우는 리액트", 50_000, "REACT", "#IT #REACT");
            Product product4 = productService.create(member2, "유튜브 클론을 통해 배우는 HTML", 60_000, "HTML", "#IT #HTML");

        };
    }
}
