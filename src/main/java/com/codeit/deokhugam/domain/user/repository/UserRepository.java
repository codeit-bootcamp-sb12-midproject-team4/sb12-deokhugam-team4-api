package com.codeit.deokhugam.domain.user.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.codeit.deokhugam.domain.user.User;

public interface UserRepository extends JpaRepository<User, UUID> {

}
