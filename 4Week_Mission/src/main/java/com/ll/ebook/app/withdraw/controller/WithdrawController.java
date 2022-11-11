package com.ll.ebook.app.withdraw.controller;

import com.ll.ebook.app.base.dto.RsData;
import com.ll.ebook.app.base.rq.Rq;
import com.ll.ebook.app.member.entity.Member;
import com.ll.ebook.app.member.service.MemberService;
import com.ll.ebook.app.postkeyword.entity.PostKeyword;
import com.ll.ebook.app.product.entity.Product;
import com.ll.ebook.app.product.form.ProductForm;
import com.ll.ebook.app.withdraw.form.WithdrawForm;
import com.ll.ebook.app.withdraw.service.WithdrawService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/withdraw")
@RequiredArgsConstructor
public class WithdrawController {

    private final WithdrawService withdrawService;
    private final MemberService memberService;
    private final Rq rq;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/apply")
    public String showApply(Model model) {
        long actorRestCash = memberService.getRestCash(rq.getMember());
        model.addAttribute("actorRestCash", actorRestCash);
        return "withdraw/apply";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/apply")
    public String apply(@Valid WithdrawForm withdrawForm) {
        Member member = rq.getMember();
        RsData rsData = withdrawService.create(member, withdrawForm.getBankName(), withdrawForm.getBankAccountNo(), withdrawForm.getPrice());

        if (rsData.isFail()) {
            return Rq.redirectWithErrorMsg("/withdraw/apply", rsData);
        }

        return Rq.redirectWithMsg("/withdraw/apply", rsData);
    }
}
