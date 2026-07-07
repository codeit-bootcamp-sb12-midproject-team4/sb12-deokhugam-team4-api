package com.codeit.deokhugam.domain.booksearch;

import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.CompletionField;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.InnerField;
import org.springframework.data.elasticsearch.annotations.MultiField;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Document(indexName = "books")
public class BookDocument {

	@Id
	private String id;

	@MultiField(
		mainField = @Field(type = FieldType.Text, analyzer = "nori"), // 검색용
		otherFields = {
			@InnerField(suffix = "keyword", type = FieldType.Keyword) // 정렬용
		}
	)
	private String title;

	@Field(type = FieldType.Text, analyzer = "nori") // -> Text - 가중치용(유사도)
	private String author;

	@Field(type = FieldType.Text, analyzer = "nori")
	private String description;

	@CompletionField(analyzer = "nori") // -> 검색어 자동완성용 필드
	private String titleSuggest;

	@MultiField(
		mainField = @Field(type = FieldType.Text, analyzer = "nori"), // 사용자가 '민음사'만 쳐도 검색되게!
		otherFields = { @InnerField(suffix = "keyword", type = FieldType.Keyword) }
	)
	private String publisher;

	@MultiField(
		mainField = @Field(type = FieldType.Text, analyzer = "nori"), // 사용자가 '소설'만 쳐도 검색되게!
		otherFields = { @InnerField(suffix = "keyword", type = FieldType.Keyword) }
	)
	private String categoryPath;

	@Field(type = FieldType.Long) // -> Long/Double/Date - 범위필터링&정렬/집계용
	private Long reviewCount;

	@Field(type = FieldType.Double)
	private Double rating;

	@Field(type = FieldType.Date, format = {}, pattern = "uuuu-MM-dd")
	private String publishedDate;

	@Field(type = FieldType.Keyword)
	private String isbn;

	@Field(type = FieldType.Keyword, index = false)
	private String thumbnailKey;

	@Field(type = FieldType.Date, format = {}, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSSSSX")
	private Instant createdAt;

	@Field(type = FieldType.Date, format = {}, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSSSSX")
	private Instant updatedAt;

}
