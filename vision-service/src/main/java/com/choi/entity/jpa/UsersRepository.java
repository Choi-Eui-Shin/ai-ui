package com.choi.entity.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import com.choi.entity.Users;

public interface UsersRepository extends JpaRepository<Users, String> {
	public Users findByUserIdAndUserPassword(String userId, String userPassword);
}
