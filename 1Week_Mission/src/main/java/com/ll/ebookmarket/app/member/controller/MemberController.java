package com.ll.ebookmarket.app.member.controller;

import com.ll.ebookmarket.app.mail.dto.MailDto;
import com.ll.ebookmarket.app.mail.service.MailService;
import com.ll.ebookmarket.app.member.dto.*;
import com.ll.ebookmarket.app.member.entity.Member;
import com.ll.ebookmarket.app.member.exception.MemberNotFoundException;
import com.ll.ebookmarket.app.member.service.MemberService;
import com.ll.ebookmarket.app.security.dto.MemberContext;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Controller
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;
    private final MailService mailService;

    @GetMapping("/login")
    public String login() {
        return "member/login";
    }

    @GetMapping("/join")
    public String join(MemberCreateForm memberCreateForm) {
        return "member/join";
    }

    @PostMapping("/join")
    public String join(@Valid MemberCreateForm memberCreateForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "member/join";
        }

        if (!memberCreateForm.getPassword1().equals(memberCreateForm.getPassword2())) {
            bindingResult.rejectValue("password2", "passwordInCorrect",
                    "2개의 패스워드가 일치하지 않습니다.");
            return "member/join";
        }

        try {
            memberService.create(memberCreateForm.getUsername(), memberCreateForm.getPassword1(),
                    memberCreateForm.getEmail(), memberCreateForm.getNickname());
        }catch(DataIntegrityViolationException e) {
            e.printStackTrace();
            bindingResult.reject("joinFailed", "이미 등록된 사용자입니다.");
            return "member/join";
        }catch(Exception e) {
            e.printStackTrace();
            bindingResult.reject("joinFailed", e.getMessage());
            return "member/join";
        }

        MailDto mailDto = MailDto.builder()
                .title("[ebookMarket] 회원 가입이 완료되었습니다.")
                .address(memberCreateForm.getEmail())
                .content("%s 님의 사이트 가입을 진심으로 축하드립니다.".formatted(memberCreateForm.getUsername()))
                .build();

        mailService.sendMail(mailDto);

        return "redirect:/";
    }

    @GetMapping("/findUsername")
    public String findUsername(FindUsernameForm findUsernameForm) {
        return "member/findUsername";
    }

    @PostMapping("/findUsername")
    public String findUsername(Model model, @Valid FindUsernameForm findUsernameForm, BindingResult bindingResult) {
        try {
            List<Member> memberList = memberService.findByEmail(findUsernameForm.getEmail());
            List<String> usernameList = memberList.stream().map(Member::getUsername).collect(Collectors.toList());

            model.addAttribute("usernameList", usernameList);
            return "member/findUsernameSuccess";
        } catch(MemberNotFoundException e) {
            e.printStackTrace();
            bindingResult.reject("findUsernameFailed", "해당 이메일로 가입한 아이디를 찾을 수 없습니다.");
            return "member/findUsername";
        }
    }

    @GetMapping("/findPassword")
    public String findPassword(FindPasswordForm findPasswordForm) {
        return "member/findPassword";
    }

    @PostMapping("/findPassword")
    public String findPassword(Model model, @Valid FindPasswordForm findPasswordForm, BindingResult bindingResult) {
        try {
            Member member = memberService.findByUsername(findPasswordForm.getUsername());
            if (!findPasswordForm.getEmail().equals(member.getEmail())) {
                bindingResult.reject("emailIsNotEqual", "아이디의 이메일과 입력하신 이메일이 일치하지 않습니다");
                return "member/findPassword";
            }

            String newPassword = UUID.randomUUID().toString().substring(0,16);

            memberService.modifyPassword(member, newPassword);

            MailDto mailDto = MailDto.builder()
                    .title("[ebookMarket] 임시 비밀번호 발송")
                    .address(findPasswordForm.getEmail())
                    .content("새 비밀번호: %s".formatted(newPassword))
                    .build();

            mailService.sendMail(mailDto);

            model.addAttribute("message", "%s 계정으로 임시 비밀번호를 발송하였습니다.".formatted(member.getEmail()));
            return "member/findPasswordSuccess";
        } catch (MemberNotFoundException e) {
            e.printStackTrace();
            bindingResult.reject("findUsernameFailed", "해당 이메일로 가입한 아이디를 찾을 수 없습니다.");
            return "member/findPassword";
        }
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/profile")
    public String getProfile() {
        return "member/profile";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify")
    public String modify(MemberModifyForm memberModifyForm) {
        return "member/modify";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify")
    public String modify(Principal principal, @Valid MemberModifyForm memberModifyForm, BindingResult bindingResult) {
        if(memberModifyForm.getNickname().isBlank() && memberModifyForm.getEmail().isBlank()){
            bindingResult.reject("memberModifyFormIsAllNull", "수정하고 싶은 값을 넣어주세요.");
            return "member/modify";
        }

        Member member = memberService.findByUsername(principal.getName());

        memberService.modify(member, memberModifyForm.getEmail(), memberModifyForm.getNickname());
        return "member/profile";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modifyPassword")
    public String modifyPassword(MemberModifyPasswordForm memberModifyPasswordForm) {
        return "member/modifyPassword";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modifyPassword")
    public String modifyPassword(Principal principal, @Valid MemberModifyPasswordForm memberModifyPasswordForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "member/modifyPassword";
        }

        Member member = memberService.findByUsername(principal.getName());

        if (!memberService.checkPassword(member, memberModifyPasswordForm.getOldPassword())) {
            bindingResult.rejectValue("oldPassword", "passwordInCorrect",
                    "이전 비밀번호가 일치하지 않습니다.");
            return "member/modifyPassword";
        }

        if (!memberModifyPasswordForm.getPassword().equals(memberModifyPasswordForm.getPasswordConfirm())) {
            bindingResult.rejectValue("passwordConfirm", "passwordInCorrect",
                    "2개의 패스워드가 일치하지 않습니다.");
            return "member/modifyPassword";
        }

        memberService.modifyPassword(member, memberModifyPasswordForm.getPassword());

        return "member/profile";
    }
}
