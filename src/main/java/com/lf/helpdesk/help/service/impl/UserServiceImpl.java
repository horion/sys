package com.lf.helpdesk.help.service.impl;

import com.lf.helpdesk.help.entity.User;
import com.lf.helpdesk.help.repository.RepositoryUser;
import com.lf.helpdesk.help.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
public class UserServiceImpl implements UserService {


    private final RepositoryUser repositoryUser;

    @Autowired
    public UserServiceImpl(RepositoryUser repositoryUser) {
        this.repositoryUser = repositoryUser;
    }

    @Override
    public User findByEmail(String email) {
        return repositoryUser.findByEmail(email);
    }

    @Override
    public User createOrUpdate(User user) {
        return repositoryUser.save(user);
    }

    @Override
    public User findById(String id) {
        return repositoryUser.findById(id).orElse(null);
    }

    @Override
    public void delete(String id) {
        repositoryUser.deleteById(id);
    }

    @Override
    public Page<User> findAll(int page, int count) {
        Pageable pageable = PageRequest.of(page,count);
        return repositoryUser.findAll(pageable);
    }
}
