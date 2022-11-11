package com.ll.ebook.app.product.dto;

import com.ll.ebook.app.myBook.dto.BookChapterDto;
import com.ll.ebook.app.post.entity.Post;
import com.ll.ebook.app.product.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class ProductDetailDto {
    private Long id;
    private LocalDateTime createDate;
    private LocalDateTime modifyDate;
    private Long authorId;
    private String authorName;
    private String subject;
    private List<BookChapterDto> bookChapters;

    public static ProductDetailDto of(Product product, List<Post> posts) {
        return new ProductDetailDto(
                product.getId(),
                product.getCreateDate(),
                product.getModifyDate(),
                product.getAuthor().getId(),
                product.getAuthor().getName(),
                product.getSubject(),
                posts.stream()
                        .map(post -> BookChapterDto.of(post))
                        .toList()
        );
    }
}
