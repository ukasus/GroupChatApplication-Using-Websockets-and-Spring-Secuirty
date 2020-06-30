package com.ujjawal.projects.chatApplication.Repo;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.ujjawal.projects.chatApplication.Entity.UserInfo;




public interface UserRepository extends MongoRepository<UserInfo, String> {

	UserInfo findByEmail(String email);
}
