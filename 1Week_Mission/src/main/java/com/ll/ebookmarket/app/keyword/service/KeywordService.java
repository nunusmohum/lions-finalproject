package com.ll.ebookmarket.app.keyword.service;

import com.ll.ebookmarket.app.keyword.entity.Keyword;
import com.ll.ebookmarket.app.keyword.repository.KeywordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class KeywordService {
    private final KeywordRepository keywordRepository;

    public Keyword save(String keywordContent) {
        Optional<Keyword> optKeyword = keywordRepository.findByContent(keywordContent);

        if ( optKeyword.isPresent() ) {
            return optKeyword.get();
        }

        Keyword keyword = Keyword
                .builder()
                .content(keywordContent)
                .build();

        keywordRepository.save(keyword);

        return keyword;
    }

    public Keyword findById(Long keywordId) {
        Optional<Keyword> optKeyword = keywordRepository.findById(keywordId);

        return optKeyword.orElse(null);
    }

    public Keyword findByContent(String content) {
        Optional<Keyword> optKeyword = keywordRepository.findByContent(content);

        return optKeyword.orElse(null);
    }
}
