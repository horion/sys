package com.lf.helpdesk.help.repository;

import com.lf.helpdesk.help.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RepositoryUser extends MongoRepository<User,String> {

    User findByEmail(String email);



}
