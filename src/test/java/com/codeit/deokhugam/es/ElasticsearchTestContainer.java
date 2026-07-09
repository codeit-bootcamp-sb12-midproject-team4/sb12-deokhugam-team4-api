package com.codeit.deokhugam.es;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

// ES가 필요한 프로필의 경우 해당 추상클래스 상속 및 프로필(test-es)사용
// 추가 이미지는 도커 이미지를 추가로 로드하여서 사용
@Testcontainers
public abstract class ElasticsearchTestContainer {

	@Container
	static final ElasticsearchContainer elasticsearch =
		new ElasticsearchContainer("docker.elastic.co/elasticsearch/elasticsearch:8.15.0")
			.withEnv("xpack.security.enabled", "false");

	@DynamicPropertySource
	static void elasticsearchProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.elasticsearch.uris", elasticsearch::getHttpHostAddress);
	}
}