package com.codeit.deokhugam.domain.booksearch;

import java.time.Instant;
import java.time.LocalDate;

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
		mainField = @Field(type = FieldType.Text, analyzer = "korean_analyzer"),
		otherFields = {
			@InnerField(suffix = "keyword", type = FieldType.Keyword)
		}
	)
	private String title;

	@Field(type = FieldType.Text, analyzer = "korean_analyzer")
	private String author;

	@Field(type = FieldType.Text, analyzer = "korean_analyzer")
	private String description;

	@CompletionField(analyzer = "korean_analyzer")
	private String titleSuggest;

	@MultiField(
		mainField = @Field(type = FieldType.Text, analyzer = "korean_analyzer"),
		otherFields = { @InnerField(suffix = "keyword", type = FieldType.Keyword) }
	)
	private String publisher;

	@MultiField(
		mainField = @Field(type = FieldType.Text, analyzer = "korean_analyzer"),
		otherFields = { @InnerField(suffix = "keyword", type = FieldType.Keyword) }
	)
	private String categoryPath;

	@Field(type = FieldType.Long)
	private Long reviewCount;

	@Field(type = FieldType.Double)
	private Double rating;

	@Field(type = FieldType.Date, format = {}, pattern = "uuuu-MM-dd")
	private LocalDate publishedDate;

	@Field(type = FieldType.Keyword)
	private String isbn;

	@Field(type = FieldType.Keyword, index = false)
	private String thumbnailKey;

	@Field(type = FieldType.Date, format = {}, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSSSSX")
	private Instant createdAt;

	@Field(type = FieldType.Date, format = {}, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSSSSX")
	private Instant updatedAt;

}
