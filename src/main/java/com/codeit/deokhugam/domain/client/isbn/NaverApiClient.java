package com.codeit.deokhugam.domain.client.isbn;

import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponentsBuilder;

import com.codeit.deokhugam.domain.book.dto.BookIsbnResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class NaverApiClient implements IsbnClient {
	private final RestClient restClient;
	@Value("${NAVER_API_CLIENT_URI}")
	private String clientUri;
	@Value("${NAVER_API_CLIENT_ID}")
	private String clientId;
	@Value("${NAVER_API_CLIENT_SECRET}")
	private String clientSecret;

	public NaverApiClient(RestClient.Builder restClientBuilder) {
		this.restClient = restClientBuilder.build();
	}

	@Override
	public BookIsbnResponse getInfoFromIsbn(String isbn) {
		URI uri = UriComponentsBuilder
			.fromUriString(clientUri)
			.queryParam("query", isbn)
			.build()
			.toUri();

		NaverApiResponse response = null;
		try {
			response = restClient.get()
				.uri(uri)
				.header("Host", "openapi.naver.com")
				.header("User-Agent", "curl/7.49.1")
				.header("Accept", "*/*")
				.header("X-Naver-Client-Id", clientId)
				.header("X-Naver-Client-Secret", clientSecret)
				.retrieve()
				.body(NaverApiResponse.class);
		} catch (HttpClientErrorException | HttpServerErrorException e) {
			log.error("Naver API HTTP Error: status={}, body={}", e.getStatusCode(), e.getResponseBodyAsString(), e);
		} catch (RestClientException e) {
			log.error("Naver API Network Error: {}", e.getMessage(), e);
		} catch (Exception e) {
			log.error("Naver API Unknown Error: {}", e.getMessage(), e);
		}

		if (response != null && response.getItems() != null && !response.getItems().isEmpty()) {
			NaverApiResponseItem item = response.getItems().get(0);
			return BookIsbnResponse.builder()
				.title(item.getTitle())
				.author(item.getAuthor())
				.description(item.getDescription())
				.publisher(item.getPublisher())
				.publishedDate(LocalDate.parse(item.getPubdate(), DateTimeFormatter.ofPattern("yyyyMMdd")))
				.isbn(item.getIsbn())
				.thumbnailImage(item.getImage())
				.build();
		}
		return null;
	}

}
