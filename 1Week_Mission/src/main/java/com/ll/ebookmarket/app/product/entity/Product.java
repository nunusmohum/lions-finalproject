package com.ll.ebookmarket.app.product.entity;

import com.ll.ebookmarket.app.base.entity.BaseEntity;
import com.ll.ebookmarket.app.keyword.entity.Keyword;
import com.ll.ebookmarket.app.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Product extends BaseEntity {
    @ManyToOne
    private Member author;

    @ManyToOne
    private Keyword keyword;

    private String subject;

    private Integer price;
}
