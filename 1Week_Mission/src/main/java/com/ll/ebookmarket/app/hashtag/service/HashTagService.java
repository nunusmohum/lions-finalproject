package com.ll.ebookmarket.app.hashtag.service;

import com.ll.ebookmarket.app.hashtag.entity.HashTag;
import com.ll.ebookmarket.app.hashtag.repository.HashTagRepository;
import com.ll.ebookmarket.app.keyword.entity.Keyword;
import com.ll.ebookmarket.app.keyword.service.KeywordService;
import com.ll.ebookmarket.app.post.entity.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HashTagService {
    private final KeywordService keywordService;
    private final HashTagRepository hashTagRepository;

    public void applyHashTags(Post post, String hashTagContents) {
        List<HashTag> oldHashTags = getHashTags(post);

        List<String> keywordContents = Arrays.stream(hashTagContents.split("#"))
                .map(String::trim)
                .filter(s -> s.length() > 0).toList();

        List<HashTag> needToDelete = new ArrayList<>();

        for (HashTag oldHashTag : oldHashTags) {
            boolean contains = keywordContents.stream().anyMatch(s -> s.equals(oldHashTag.getKeyword().getContent()));

            if (!contains) {
                needToDelete.add(oldHashTag);
            }
        }

        hashTagRepository.deleteAll(needToDelete);

        keywordContents.forEach(keywordContent -> {
            saveHashTag(post, keywordContent);
        });
    }

    private HashTag saveHashTag(Post post, String keywordContent) {
        Keyword keyword = keywordService.save(keywordContent);

        Optional<HashTag> opHashTag = hashTagRepository.findByPostIdAndKeywordId(post.getId(), keyword.getId());

        if (opHashTag.isPresent()) {
            return opHashTag.get();
        }

        HashTag hashTag = HashTag.builder()
                .post(post)
                .keyword(keyword)
                .build();

        hashTagRepository.save(hashTag);

        return hashTag;
    }

    public List<HashTag> getHashTags(Post post) {
        return hashTagRepository.findAllByPostId(post.getId());
    }

    public List<HashTag> getHashTagsByPostIdIn(long[] ids) {
        return hashTagRepository.findAllByPostIdIn(ids);
    }
}
