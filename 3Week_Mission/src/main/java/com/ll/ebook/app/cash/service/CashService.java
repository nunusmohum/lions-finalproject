package com.ll.ebook.app.cash.service;

import com.ll.ebook.app.cash.entity.CashLog;
import com.ll.ebook.app.member.entity.Member;
import com.ll.ebook.app.cash.repository.CashLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CashService {
    private final CashLogRepository cashLogRepository;

    public CashLog addCash(Member member, long price, String eventType) {
        CashLog cashLog = CashLog.builder()
                .member(member)
                .price(price)
                .eventType(eventType)
                .build();

        cashLogRepository.save(cashLog);

        return cashLog;
    }
}
