package com.lf.helpdesk.help.security.jwt;

import com.lf.helpdesk.help.entity.User;
import com.lf.helpdesk.help.enums.EnumProfile;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class JwtUserFactory {


    private JwtUserFactory(){

    }

    public static JwtUser create(User user){
        return new JwtUser(user.getId(),user.getEmail(),user.getPassword(),mapToGrantedAuthorities(user.getProfile()));
    }

    private static Collection<? extends GrantedAuthority> mapToGrantedAuthorities(EnumProfile profile) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(profile.toString()));
        return authorities;
    }

}
