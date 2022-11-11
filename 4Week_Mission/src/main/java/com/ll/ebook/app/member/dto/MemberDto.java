package com.ll.ebook.app.member.dto;

import com.ll.ebook.app.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class MemberDto {
    private Long id;
    private LocalDateTime createDate;
    private LocalDateTime modifyDate;
    private String username;
    private String email;
    private boolean emailVerified;
    private String nickname;

    public static MemberDto of(Member member) {
        return new MemberDto(
                member.getId(),
                member.getCreateDate(),
                member.getModifyDate(),
                member.getUsername(),
                member.getEmail(),
                member.isEmailVerified(),
                member.getNickname()
        );
    }
}
