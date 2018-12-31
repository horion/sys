package com.lf.helpdesk.help.security.model;

import com.lf.helpdesk.help.entity.User;

public class CurrentUser {

    private String token;

    public CurrentUser(String token, User user) {
        this.token = token;
        this.user = user;
    }

    private User user;


    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
