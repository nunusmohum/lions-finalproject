package com.ll.ebookmarket.app.product.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Getter
@Setter
public class ProductForm {
    @NotEmpty(message="제목은 필수항목입니다.")
    @Size(max=255)
    private String subject;

    @NotEmpty(message="가격은 필수항목입니다.")
    private String price;

    @NotEmpty(message="키워드는는 반드시 선택하여야 합니다.")
    private String keywordContent;
}
