package com.ll.ebook.app.myBook.dto;

import com.ll.ebook.app.member.dto.MemberDto;
import com.ll.ebook.app.member.entity.Member;
import com.ll.ebook.app.myBook.entity.MyBook;
import com.ll.ebook.app.product.dto.ProductDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.bytebuddy.asm.Advice;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class MyBookDto {
    private Long id;
    private LocalDateTime createDate;
    private LocalDateTime modifyDate;
    private Long ownerId;
    private ProductDto product;

    public static MyBookDto of(MyBook myBook) {
        return new MyBookDto(
                myBook.getId(),
                myBook.getCreateDate(),
                myBook.getModifyDate(),
                myBook.getProduct().getId(),
                ProductDto.of(myBook.getProduct())
        );
    }
}
