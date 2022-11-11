package com.ll.ebookmarket.app.post.repository;

import com.ll.ebookmarket.app.post.entity.Post;

import java.util.List;

public interface PostRepositoryCustom {
    List<Post> getQslPostsOrderByIdDesc();

    List<Post> searchQsl(String kwType, String kw);
}
