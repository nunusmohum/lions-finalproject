package com.ll.ebookmarket.app.post.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Getter
@Setter
public class PostForm {
    @NotEmpty(message="제목은 필수항목입니다.")
    @Size(max=255)
    private String subject;

    @NotEmpty(message="내용은 필수항목입니다.")
    private String content;

    private String contentHtml;

    @NotEmpty(message="해쉬태그는 하나 이상 등록되어야 합니다.")
    private String hashTagContents;
}