package com.ll.ebookmarket.app.postkeyword.repository;

import com.ll.ebookmarket.app.postkeyword.entity.PostKeyword;

import java.util.List;

public interface PostKeywordRepositoryCustom {
    List<PostKeyword> getQslAllByAuthorId(Long authorId);
}
