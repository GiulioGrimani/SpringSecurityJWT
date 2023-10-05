package com.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.entity.UserInfo;

public interface UserInfoRepository extends JpaRepository<UserInfo, Integer> {

	@Query("SELECT u FROM UserInfo u WHERE email = :principal OR username = :principal")
	public Optional<UserInfo> findByEmailOrUsername(String principal);

	public Optional<UserInfo> findByEmail(String email);

	public Optional<UserInfo> findByUsername(String username);

}
