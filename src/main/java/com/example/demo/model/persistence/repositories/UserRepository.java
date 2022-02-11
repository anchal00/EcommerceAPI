package com.example.demo.model.persistence.repositories;

import com.example.demo.model.persistence.User;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
	User findByUsername(String username);
}
