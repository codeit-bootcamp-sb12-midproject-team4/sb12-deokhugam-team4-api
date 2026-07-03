package com.codeit.deokhugam.domain.user.repository;

import com.codeit.deokhugam.domain.user.OauthKakao;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OauthKakaoRepository extends JpaRepository<OauthKakao, java.util.UUID> {

  Optional<OauthKakao> findByKakaoId(String kakaoId);
}
