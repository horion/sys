package com.lf.helpdesk.help.security.jwt;

import java.io.Serializable;

public class JwtAuthenticationRequest implements Serializable {
    private static final long serialVersionUID = 5382238791650489012L;

    private String email;
    private String password;

    public JwtAuthenticationRequest(){
        super();
    }

    public JwtAuthenticationRequest(String email,String password){
        this.setEmail(email);
        this.setPassword(password);
    }

    private void setPassword(String password) {
        this.password = password;
    }

    private void setEmail(String email) {
        this.email=email;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
