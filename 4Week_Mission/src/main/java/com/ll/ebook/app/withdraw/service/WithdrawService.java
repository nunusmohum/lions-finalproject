package com.ll.ebook.app.withdraw.service;

import com.ll.ebook.app.base.dto.RsData;
import com.ll.ebook.app.member.entity.Member;
import com.ll.ebook.app.member.service.MemberService;
import com.ll.ebook.app.order.entity.Order;
import com.ll.ebook.app.post.entity.Post;
import com.ll.ebook.app.product.entity.Product;
import com.ll.ebook.app.withdraw.entity.Withdraw;
import com.ll.ebook.app.withdraw.form.WithdrawForm;
import com.ll.ebook.app.withdraw.repository.WithdrawRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WithdrawService {

    private final WithdrawRepository withdrawRepository;
    private final MemberService memberService;

    public Optional<Withdraw> findById(long withdrawId) {
        return withdrawRepository.findById(withdrawId);
    }

    public List<Withdraw> findAll() {
        return withdrawRepository.findAllByOrderByIdDesc();
    }

    @Transactional
    public RsData create(Member member, String bankName, String bankAccountNo, int price) {
        long actorRestCash = memberService.getRestCash(member);
        if(price > actorRestCash) {
            return RsData.of("F-1", "예치금이 부족합니다.");
        }

        Withdraw withdraw = Withdraw
                .builder()
                .bankName(bankName)
                .bankAccountNo(bankAccountNo)
                .price(price)
                .applicant(member)
                .build();

        withdrawRepository.save(withdraw);

        return RsData.of("S-1", "출금 신청이 정상적으로 완료되었습니다.");
    }

    @Transactional
    public RsData<String> apply(Withdraw withdraw) {
        Member applicant = withdraw.getApplicant();
        long restCash = memberService.getRestCash(applicant);
        if (withdraw.getPrice() > restCash) {
            return RsData.of("F-1", "예치금이 부족합니다.");
        }

        memberService.addCash(applicant, withdraw.getPrice() * -1, "출금__%s__%s".formatted(withdraw.getBankName(), withdraw.getBankAccountNo()));

        withdraw.setApplyDone();

        return RsData.of("S-1", "출금 처리가 완료되었습니다.");
    }
}
