package com.lf.helpdesk.help;

import com.lf.helpdesk.help.entity.User;
import com.lf.helpdesk.help.enums.EnumProfile;
import com.lf.helpdesk.help.repository.RepositoryUser;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class HelpApplication {

    public static void main(String[] args) {
        SpringApplication.run(HelpApplication.class, args);
    }

    @Bean
    CommandLineRunner init(RepositoryUser user, PasswordEncoder passwordEncoder){
        return args -> {initUsers(user,passwordEncoder);};
    }

    private void initUsers(RepositoryUser userRepository, PasswordEncoder passwordEncoder){
        User admin = new User();
        admin.setEmail("luizfsantoss@gmail.com");
        admin.setPassword(passwordEncoder.encode("123456"));
        admin.setProfile(EnumProfile.ROLE_ADMIN);
        User find = userRepository.findByEmail("luizfsantoss@gmail.com");
        if(find == null){
            userRepository.save(admin);
        }
    }
}
