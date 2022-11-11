package com.ll.ebook.app.myBook.dto;

import com.ll.ebook.app.myBook.entity.MyBook;
import com.ll.ebook.app.post.entity.Post;
import com.ll.ebook.app.product.dto.ProductDetailDto;
import com.ll.ebook.app.product.dto.ProductDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class MyBookDetailDto {
    private Long id;
    private LocalDateTime createDate;
    private LocalDateTime modifyDate;
    private Long ownerId;
    private ProductDetailDto product;

    public static MyBookDetailDto of(MyBook myBook, List<Post> posts) {
        return new MyBookDetailDto(
                myBook.getId(),
                myBook.getCreateDate(),
                myBook.getModifyDate(),
                myBook.getProduct().getId(),
                ProductDetailDto.of(myBook.getProduct(), posts)
        );
    }
}
