package com.ll.ebookmarket.app.member.dto;

import lombok.Data;
import lombok.Getter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Getter
@Data
public class FindUsernameForm {
    @NotEmpty(message = "이메일은 필수항목입니다.")
    @Email(message = "올바른 이메일 형식으로 입력해주세요.")
    private String email;
}
