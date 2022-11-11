package com.ll.ebook.app.withdraw.controller;

import com.ll.ebook.app.base.dto.RsData;
import com.ll.ebook.app.base.rq.Rq;
import com.ll.ebook.app.withdraw.entity.Withdraw;
import com.ll.ebook.app.withdraw.service.WithdrawService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/adm/withdraw")
@RequiredArgsConstructor
public class AdmWithdrawController {

    private final WithdrawService withdrawService;
    private final Rq rq;

    @GetMapping("/applyList")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String showApplyList(Model model) {
        List<Withdraw> withdraws = withdrawService.findAll();
        model.addAttribute("withdraws", withdraws);
        return "adm/withdraw/applyList";
    }

    @PostMapping("/{withdrawApplyId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String withdrawApplyId(@PathVariable long withdrawApplyId) {
        Withdraw withdraw = withdrawService.findById(withdrawApplyId).get();
        RsData rsData = withdrawService.apply(withdraw);

        if (rsData.isFail()) {
            return Rq.redirectWithErrorMsg("/adm/withdraw/applyList", rsData);
        }

        return Rq.redirectWithMsg("/adm/withdraw/applyList", rsData);
    }
}
