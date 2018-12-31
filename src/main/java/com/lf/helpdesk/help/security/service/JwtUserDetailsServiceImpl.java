package com.lf.helpdesk.help.security.service;

import com.lf.helpdesk.help.entity.User;
import com.lf.helpdesk.help.security.jwt.JwtUserFactory;
import com.lf.helpdesk.help.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class JwtUserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService.findByEmail(username);
        if(user == null){
            throw new UsernameNotFoundException(String.format("No user found whit username '%s'.",username));
        }else {
            return JwtUserFactory.create(user);
        }
    }
}
