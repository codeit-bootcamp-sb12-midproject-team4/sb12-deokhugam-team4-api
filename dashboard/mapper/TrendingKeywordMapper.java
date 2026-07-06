package com.codeit.deokhugam.domain.dashboard.mapper;

import com.codeit.deokhugam.domain.dashboard.dto.response.KeywordListResponse;
import com.codeit.deokhugam.domain.dashboard.entity.TrendingKeyword;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TrendingKeywordMapper {

    public KeywordListResponse toKeywordListResponse(
        List<TrendingKeyword> keywords
    ) {

        return new KeywordListResponse(
            keywords.stream()
                .map(TrendingKeyword::getKeyword)
                .toList()

        );
    }
}
