package com.codeit.deokhugam.domain.notification.event;

import static org.mockito.Mockito.*;

import java.util.UUID;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import com.codeit.deokhugam.domain.notification.service.NotificationService;

//서비스 테스트가 아니라
//이벤트 생성후 transaction이 끝나면 이벤트 리스너가 동작.
//동작 함에 따라 내부 로직을 실행하는데, 서비스의 생성을 요청하는 것으로 끝.
//그렇기 떄문에 현재 단계에서 서비스 로직은 구현 전 이지만
//호출 여부가 체크가 목적이기 때문에 mock으로 진행해도 서비스 코드 미 구현이여도 문제 없다.
@SpringJUnitConfig(classes = {
	NotificationEventListener.class,
	EventListenerTest.TestConfig.class
})
@Transactional
public class EventListenerTest {

	@Autowired
	private ApplicationEventPublisher eventPublisher;

	//  서비스 구현전
	//  config로 mock을 주입하여 테스트.
	@Autowired
	private NotificationService notificationService;

	private UUID reviewId;
	private UUID userId;

	@BeforeEach
	void setUp() {
		reset(notificationService);
		reviewId = UUID.randomUUID();
		userId = UUID.randomUUID();
	}

	@Test
	@DisplayName("댓글 생성 이벤트를 발행하면 커밋 후 알림 서비스가 호출된다")
	void publishCommentCreatedEvent() {
		CommentCreatedEvent event = new CommentCreatedEvent(reviewId, userId);

		//   실제 서비스에서 각 항목의 생성 타임에 eventPublisher로 이벤트 생성
		eventPublisher.publishEvent(event);
		verifyNoInteractions(notificationService);

		commit();
		//    커밋을 한 후 이벤트 리스너가 실행.

		verify(notificationService).saveCommentCreatedNotification(event);
		//    리스너가 실행하는 메서드 실행.
	}

	@Test
	@DisplayName("리뷰 좋아요 이벤트를 발행하면 커밋 후 알림 서비스가 호출된다")
	void publishReviewLikedEvent() {
		ReviewLikedEvent event = new ReviewLikedEvent(reviewId, userId);

		eventPublisher.publishEvent(event);
		verifyNoInteractions(notificationService);

		commit();

		verify(notificationService).saveReviewLikedNotification(event);
	}

	@Test
	@DisplayName("인기 리뷰 선정 이벤트를 발행하면 커밋 후 알림 서비스가 호출된다")
	void publishPopularReviewSelectedEvent() {
		PopularReviewSelectedEvent event = new PopularReviewSelectedEvent(reviewId, null);

		eventPublisher.publishEvent(event);
		verifyNoInteractions(notificationService);

		commit();

		verify(notificationService).savePopularReviewSelectedNotification(event);
	}

	private void commit() {
		TestTransaction.flagForCommit();
		TestTransaction.end();
	}

	//  TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	//  Listener의 트랙젝션을 테스트 하기 위해 config 구현.
	//  application_test로 db 설정하면 생략가능
	@Configuration
	@EnableTransactionManagement
	static class TestConfig {

		@Bean
		NotificationService notificationService() {
			return Mockito.mock(NotificationService.class);
		}

		@Bean
		DataSource dataSource() {
			return new DriverManagerDataSource("jdbc:h2:mem:event-listener-test;DB_CLOSE_DELAY=-1");
		}

		@Bean
		PlatformTransactionManager transactionManager(DataSource dataSource) {
			return new DataSourceTransactionManager(dataSource);
		}
	}
}
