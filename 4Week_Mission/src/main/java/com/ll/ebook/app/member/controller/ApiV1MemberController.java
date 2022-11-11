package com.ll.ebook.app.member.controller;

import com.ll.ebook.app.base.dto.RsData;
import com.ll.ebook.app.base.rq.Rq;
import com.ll.ebook.app.member.dto.LoginRequest;
import com.ll.ebook.app.member.dto.LoginResponse;
import com.ll.ebook.app.member.dto.MeResponse;
import com.ll.ebook.app.member.dto.MemberDto;
import com.ll.ebook.app.member.entity.Member;
import com.ll.ebook.app.member.service.MemberService;
import com.ll.ebook.util.Ut;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static org.springframework.http.MediaType.ALL_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/api/v1/member", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Tag(name = "ApiV1MemberController", description = "로그인 및 회원정보 조회")
public class ApiV1MemberController {

    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;
    private final Rq rq;


    @PostMapping("/login")
    @Operation(summary = "로그인, 액세스 토큰 발급")
    public ResponseEntity<RsData<LoginResponse>> login(@Valid @RequestBody LoginRequest loginRequest) {
        Member member = memberService.findByUsername(loginRequest.getUsername()).orElse(null);

        if (member == null) {
            return Ut.spring.responseEntityOf(
                    RsData.of(
                            "F-2",
                            "일치하는 회원이 존재하지 않습니다."
                    )
            );
        }

        if (!passwordEncoder.matches(loginRequest.getPassword(), member.getPassword())) {
            return Ut.spring.responseEntityOf(
                    RsData.of(
                            "F-3",
                            "비밀번호가 일치하지 않습니다."
                    )
            );
        }

        String accessToken = memberService.genAccessToken(member);

        return Ut.spring.responseEntityOf(
                RsData.of(
                        "S-1",
                        "로그인 성공, Access Token을 발급합니다.",
                        new LoginResponse(accessToken)
                )
        );

    }

    @GetMapping(value = "/me", consumes = ALL_VALUE)
    @Operation(summary = "로그인 사용자 정보", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<RsData<MeResponse>> me() {
        return Ut.spring.responseEntityOf(
                RsData.successOf(
                        new MeResponse(MemberDto.of(rq.getMember()))
                )
        );
    }
}
