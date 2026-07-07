package com.codeit.deokhugam.domain.client.category;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AladinApiClient implements CategoryClient {
	private final RestClient restClient;
	@Value("${ALADIN_API_CLIENT_URI}")
	private String clientUri;
	@Value("${ALADIN_API_CLIENT_SECRET}")
	private String clientSecret;

	public AladinApiClient(RestClient.Builder restClientBuilder, ObjectMapper objectMapper) {
		MappingJackson2HttpMessageConverter myConverter = new MappingJackson2HttpMessageConverter();
		myConverter.setObjectMapper(objectMapper);
		myConverter.setSupportedMediaTypes(List.of(MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN));

		this.restClient = restClientBuilder
			.messageConverters(converters -> {
				converters.clear();
				converters.add(myConverter);
			})
			.build();
	}

	@Override
	@Retry(name = "aladinApi")
	@CircuitBreaker(name = "aladinApi")
	public String getCategoryFromIsbn(String isbn) {
		URI uri = UriComponentsBuilder
			.fromUriString(clientUri)
			.queryParam("TTBKey", clientSecret)
			.queryParam("ItemId", isbn)
			.queryParam("ItemIdType", "ISBN13")
			.queryParam("Output", "JS")
			.build()
			.toUri();

		AladinApiResponse response = null;
		try {
			response = restClient.get()
				.uri(uri)
				.retrieve()
				.body(AladinApiResponse.class);
		} catch (HttpClientErrorException | HttpServerErrorException e) {
			log.error("Aladin API HTTP Error: status={}, body={}", e.getStatusCode(), e.getResponseBodyAsString(), e);
		} catch (RestClientException e) {
			log.error("🚨 네트워크 통신 자체에서 예외 발생! ISBN: {} | 에러: {}", isbn, e.getMessage());
			return null;
		} catch (Exception e) {
			log.error("Aladin API Unknown Error: {}", e.getMessage(), e);
		}

		if (response != null && response.getItem() != null && !response.getItem().isEmpty()) {
			return response.getItem().get(0).getCategoryName();
		}
		return null;
	}

}
