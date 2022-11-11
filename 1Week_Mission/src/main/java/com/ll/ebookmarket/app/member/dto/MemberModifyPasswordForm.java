package com.ll.ebookmarket.app.member.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Data
public class MemberModifyPasswordForm {
    @NotEmpty(message = "이전 비밀번호는 필수항목입니다.")
    private String oldPassword;

    @NotEmpty(message = "새 비밀번호는 필수항목입니다.")
    private String password;

    @NotEmpty(message = "새 비밀번호 확인은 필수항목입니다.")
    private String passwordConfirm;
}
