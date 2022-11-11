package com.ll.ebookmarket.app.security.dto;

import com.ll.ebookmarket.app.member.entity.Member;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
public class MemberContext extends User {
    private final Long id;

    private final String email;

    private final String nickname;

    @Setter
    private LocalDateTime modifyDate;

    public MemberContext(Member member, List<GrantedAuthority> authorities) {
        super(member.getUsername(), member.getPassword(), authorities);
        this.id = member.getId();
        this.email = member.getEmail();
        this.nickname = member.getNickname();
        this.modifyDate = member.getModifyDate();
    }
}
