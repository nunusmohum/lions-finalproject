package com.ll.ebook.app.withdraw.entity;

import com.ll.ebook.app.base.entity.BaseEntity;
import com.ll.ebook.app.member.entity.Member;
import com.ll.ebook.app.order.entity.OrderItem;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import java.time.LocalDateTime;

import static javax.persistence.FetchType.LAZY;

@Entity
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
public class Withdraw extends BaseEntity {
    private LocalDateTime applyDate;

    private String bankName;
    private String bankAccountNo;
    private int price;

    @ManyToOne(fetch = LAZY)
    private Member applicant;

    private boolean isApplied;

    public void setApplyDone() {
        applyDate = LocalDateTime.now();
        isApplied = true;
    }

    public boolean isApplicable() {
        return !isApplied;
    }
}
