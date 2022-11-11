package com.ll.ebook.app.member.dto;

import com.ll.ebook.app.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MeResponse {
    MemberDto member;
}
