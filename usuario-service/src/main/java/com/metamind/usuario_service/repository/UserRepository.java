package com.metamind.usuario_service.repository;

import com.metamind.usuario_service.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<User, Long> {

    User findByEmail(String email);
}