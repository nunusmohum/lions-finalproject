package com.ll.ebookmarket.app.hashtag.entity;

import com.ll.ebookmarket.app.base.entity.BaseEntity;
import com.ll.ebookmarket.app.keyword.entity.Keyword;
import com.ll.ebookmarket.app.post.entity.Post;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
public class HashTag extends BaseEntity {
    @ManyToOne
    @ToString.Exclude
    private Post post;

    @ManyToOne
    @ToString.Exclude
    private Keyword keyword;
}
