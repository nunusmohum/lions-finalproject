package com.ll.ebook.app.myBook.controller;

import com.ll.ebook.app.base.dto.RsData;
import com.ll.ebook.app.base.rq.Rq;
import com.ll.ebook.app.myBook.dto.MyBookDetailDto;
import com.ll.ebook.app.myBook.dto.MyBookDto;
import com.ll.ebook.app.myBook.dto.MyBookResponse;
import com.ll.ebook.app.myBook.dto.MyBooksResponse;
import com.ll.ebook.app.myBook.entity.MyBook;
import com.ll.ebook.app.myBook.service.MyBookService;
import com.ll.ebook.app.post.entity.Post;
import com.ll.ebook.app.post.service.PostService;
import com.ll.ebook.app.product.entity.Product;
import com.ll.ebook.app.product.service.ProductService;
import com.ll.ebook.util.Ut;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

import static org.springframework.http.MediaType.ALL_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/api/v1/myBooks", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Tag(name = "ApiV1MyBooksController", description = "로그인 된 회윈이 구매한 책 정보")
public class ApiV1MyBooksController {

    private final MyBookService myBookService;
    private final ProductService productService;
    private final Rq rq;

    @GetMapping(value = "", consumes = ALL_VALUE)
    @Operation(summary =  "로그인된 회원이 보유한 도서 목록", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<RsData<MyBooksResponse>> myBooks() {
        List<MyBook> myBooks = myBookService.getMyBooksByMember(rq.getMember());
        List<MyBookDto> myBookDtos = myBooks.stream()
                .map(MyBookDto::of)
                .toList();

        return Ut.spring.responseEntityOf(
                RsData.of(
                        "S-1",
                        "회원이 보유한 도서 목록을 반환합니다.",
                        new MyBooksResponse(myBookDtos)
                )
        );

    }

    @GetMapping(value = "{myBookId}", consumes = ALL_VALUE)
    @Operation(summary =  "로그인된 회원이 보유한 도서 상세", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<RsData<MyBookResponse>> myBooks(@PathVariable Long myBookId) {
        Optional<MyBook> myBook = myBookService.getMyBookById(myBookId);
        if(myBook.isEmpty()) {
            return Ut.spring.responseEntityOf(
                    RsData.of(
                            "F-2",
                            "해당 아이디에 일치하는 책이 존재하지 않습니다"
                    )
            );
        }

        List<Post> posts = productService.findPostsByProduct(myBook.get().getProduct());


        return Ut.spring.responseEntityOf(
                RsData.of(
                        "S-1",
                        "도서의 상세정보를 반환합니다.",
                        new MyBookResponse(MyBookDetailDto.of(myBook.get(), posts))
                )
        );

    }
}
