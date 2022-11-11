package com.ll.ebook.app.product.dto;

import com.ll.ebook.app.myBook.dto.MyBookDto;
import com.ll.ebook.app.myBook.entity.MyBook;
import com.ll.ebook.app.product.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ProductDto {
    private Long id;
    private LocalDateTime createDate;
    private LocalDateTime modifyDate;
    private Long authorId;
    private String authorName;
    private String subject;

    public static ProductDto of(Product product) {
        return new ProductDto(
                product.getId(),
                product.getCreateDate(),
                product.getModifyDate(),
                product.getAuthor().getId(),
                product.getAuthor().getName(),
                product.getSubject()
        );
    }
}
