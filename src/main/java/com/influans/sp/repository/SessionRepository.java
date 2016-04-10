package com.influans.sp.repository;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import com.influans.sp.entity.Session;
import com.influans.sp.entity.User;

public interface SessionRepository extends MongoRepository<Session, String> {
	Session findSessionBySessionId(String sessionid);
	List<Session> findAll();
	List<User> findUsersBySessionId(String sessionid);
}