package com.codeit.deokhugam.domain.comment.service;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import com.codeit.deokhugam.domain.book.Book;
import com.codeit.deokhugam.domain.comment.Comment;
import com.codeit.deokhugam.domain.comment.dto.CommentCreateRequest;
import com.codeit.deokhugam.domain.comment.dto.CommentResponse;
import com.codeit.deokhugam.domain.comment.exception.CommentNotFoundException;
import com.codeit.deokhugam.domain.comment.exception.CommentNotOwnedException;
import com.codeit.deokhugam.domain.comment.repository.CommentRepository;
import com.codeit.deokhugam.domain.review.entity.Review;
import com.codeit.deokhugam.domain.user.User;
import com.codeit.deokhugam.global.config.QueryDslConfig;

@DataJpaTest(properties = {
        "spring.sql.init.mode=never",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect"
})
@Import({
        CommentService.class,
        QueryDslConfig.class
})
public class CommentServiceTest {

    @Autowired
    private CommentService commentService;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User user;
    private User otherUser;
    private Review review;

    @BeforeEach
    void setUp() {
        user = entityManager.persist(User.builder()
                .email("user@test.com")
                .nickname("user")
                .password("password")
                .build());

        otherUser = entityManager.persist(User.builder()
                .email("other@test.com")
                .nickname("other")
                .password("password")
                .build());

        Book book = entityManager.persist(Book.builder()
                .title("title")
                .author("author")
                .description("description")
                .publisher("publisher")
                .publishedDate(LocalDate.of(2024, 1, 1))
                .isbn("9781234567890")
                .thumbnailKey("thumbnail-key")
                .build());

        review = entityManager.persist(Review.builder()
                .content("review content")
                .rating(5)
                .likeCount(0L)
                .commentCount(0L)
                .book(book)
                .user(user)
                .build());

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    @DisplayName("댓글을 등록한다")
    void createComment_success() {
        CommentCreateRequest request = new CommentCreateRequest(
                review.getId(),
                user.getId(),
                "테스트 댓글입니다"
        );

        CommentResponse response = commentService.createComment(request);

        assertThat(response).isNotNull();
        assertThat(response.content()).isEqualTo("테스트 댓글입니다");
        assertThat(response.userId()).isEqualTo(user.getId());
        assertThat(response.reviewId()).isEqualTo(review.getId());
    }

    @Test
    @DisplayName("댓글 목록을 조회한다")
    void getComments_success() {
        entityManager.persist(new Comment("댓글1", user, review));
        entityManager.persist(new Comment("댓글2", user, review));
        entityManager.flush();
        entityManager.clear();

        List<CommentResponse> responses = commentService.getComments(review.getId());

        assertThat(responses).hasSize(2);
    }

    @Test
    @DisplayName("댓글 단건을 조회한다")
    void getComment_success() {
        Comment comment = entityManager.persist(new Comment("댓글", user, review));
        entityManager.flush();
        entityManager.clear();

        CommentResponse response = commentService.getComment(comment.getId());

        assertThat(response).isNotNull();
        assertThat(response.content()).isEqualTo("댓글");
    }

    @Test
    @DisplayName("존재하지 않는 댓글 조회 시 예외가 발생한다")
    void getComment_notFound() {
        assertThatThrownBy(() -> commentService.getComment(UUID.randomUUID()))
                .isInstanceOf(CommentNotFoundException.class);
    }

    @Test
    @DisplayName("댓글을 논리 삭제한다")
    void deleteComment_success() {
        Comment comment = entityManager.persist(new Comment("댓글", user, review));
        entityManager.flush();
        entityManager.clear();

        commentService.deleteComment(comment.getId(), user.getId());

        Comment deleted = commentRepository.findById(comment.getId()).orElseThrow();
        assertThat(deleted.isDeleted()).isTrue();
    }

    @Test
    @DisplayName("본인 댓글이 아닌 경우 삭제 시 예외가 발생한다")
    void deleteComment_notOwned() {
        Comment comment = entityManager.persist(new Comment("댓글", user, review));
        entityManager.flush();
        entityManager.clear();

        assertThatThrownBy(() -> commentService.deleteComment(comment.getId(), otherUser.getId()))
                .isInstanceOf(CommentNotOwnedException.class);
    }

    @Test
    @DisplayName("댓글을 물리 삭제한다")
    void hardDeleteComment_success() {
        Comment comment = entityManager.persist(new Comment("댓글", user, review));
        entityManager.flush();
        entityManager.clear();

        commentService.hardDeleteComment(comment.getId());

        assertThat(commentRepository.findById(comment.getId())).isEmpty();
    }
}