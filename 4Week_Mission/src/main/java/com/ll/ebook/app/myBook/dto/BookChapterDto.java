package com.ll.ebook.app.myBook.dto;

import com.ll.ebook.app.post.entity.Post;
import com.ll.ebook.app.product.dto.ProductDetailDto;
import com.ll.ebook.app.product.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class BookChapterDto {
    private Long id;
    private String subject;
    private String content;
    private String contentHtml;

    public static BookChapterDto of(Post post) {
        return new BookChapterDto(
                post.getId(),
                post.getSubject(),
                post.getContent(),
                post.getContentHtml()
        );
    }
}
