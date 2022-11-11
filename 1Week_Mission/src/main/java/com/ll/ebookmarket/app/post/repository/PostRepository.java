package com.ll.ebookmarket.app.post.repository;

import com.ll.ebookmarket.app.member.entity.Member;
import com.ll.ebookmarket.app.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryCustom {
    List<Post> findAllByAuthor(Member author);

    List<Post> findTop100ByOrderByIdDesc();
}
