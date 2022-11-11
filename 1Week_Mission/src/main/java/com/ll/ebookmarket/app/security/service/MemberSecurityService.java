package com.ll.ebookmarket.app.security.service;

import com.ll.ebookmarket.app.member.entity.Member;
import com.ll.ebookmarket.app.member.repository.MemberRepository;
import com.ll.ebookmarket.app.security.dto.MemberContext;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberSecurityService implements UserDetailsService {
    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findByUsername(username).get();

        List<GrantedAuthority> authorities = new ArrayList<>();

        switch (member.getAuthLevel()) {
            case 3 -> authorities.add(new SimpleGrantedAuthority("USER"));
            case 4 -> authorities.add(new SimpleGrantedAuthority("AUTHOR"));
            case 7 -> authorities.add(new SimpleGrantedAuthority("ADMIN"));
        }

        return new MemberContext(member, authorities);
    }
}
