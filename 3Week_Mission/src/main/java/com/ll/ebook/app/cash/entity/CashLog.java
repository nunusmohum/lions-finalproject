package com.ll.ebook.app.cash.entity;


import com.ll.ebook.app.member.entity.Member;
import com.ll.ebook.app.base.entity.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import static javax.persistence.FetchType.LAZY;

@Entity
@Getter
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
public class CashLog extends BaseEntity {
    private String relTypeCode;
    private Long relId;
    @ManyToOne(fetch = LAZY)
    private Member member;
    private long price; // 변동
    private String eventType;

    public CashLog(long id) {
        super(id);
    }
}
