package com.lf.helpdesk.help.service;

import com.lf.helpdesk.help.entity.User;
import org.springframework.data.domain.Page;

public interface UserService {

    User findByEmail(String email);

    User createOrUpdate(User user);

    User findById(String id);

    void delete(String id);

    Page<User> findAll(int page,int count);

}
