package com.ll.ebookmarket.app.post.repository;

import com.ll.ebookmarket.app.post.entity.Post;
import com.ll.ebookmarket.util.Util;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

import static com.ll.ebookmarket.app.post.entity.QPost.post;
import static com.ll.ebookmarket.app.keyword.entity.QKeyword.keyword;
import static com.ll.ebookmarket.app.hashtag.entity.QHashTag.hashTag;

@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Post> getQslPostsOrderByIdDesc() {
        return jpaQueryFactory
                .select(post)
                .from(post)
                .orderBy(post.id.desc())
                .fetch();
    }

    @Override
    public List<Post> searchQsl(String kwType, String kw) {
        JPAQuery<Post> jpqQuery = jpaQueryFactory
                .select(post)
                .distinct()
                .from(post);

        if (!Util.str.empty(kw)) {
            if (Util.str.eq(kwType, "hashTag")) {
                jpqQuery
                        .innerJoin(hashTag)
                        .on(post.eq(hashTag.post))
                        .innerJoin(keyword)
                        .on(keyword.eq(hashTag.keyword))
                        .where(keyword.content.eq(kw));

            }
        }

        jpqQuery.orderBy(post.id.desc());

        return jpqQuery.fetch();
    }
}
