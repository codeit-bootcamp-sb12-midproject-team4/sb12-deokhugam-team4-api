package com.codeit.deokhugam.domain.user.repository;

import com.codeit.deokhugam.domain.user.User;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, UUID> {

  Optional<User> findByEmail(String email);

  Optional<User> findByEmailAndDeletedAtIsNull(String email);

}