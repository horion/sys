package com.lf.helpdesk.help.controller;

import com.lf.helpdesk.help.entity.User;
import com.lf.helpdesk.help.response.Response;
import com.lf.helpdesk.help.service.UserService;
import com.mongodb.DuplicateKeyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*")
public class UserController {


    @Autowired
    UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Response<User>> create(HttpServletRequest request, @RequestBody User user, BindingResult result){
        Response<User> response = new Response<>();
        try{
            validateCreateUser(user,result);
            if(result.hasErrors()){
                result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
                return ResponseEntity.badRequest().body(response);
            }
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            User userPersisted =  this.userService.createOrUpdate(user);
            response.setData(userPersisted);
        }catch (DuplicateKeyException e){
            response.getErrors().add("E-mail already registered !");
            return ResponseEntity.badRequest().body(response);
        }catch (Exception e){
            response.getErrors().add(e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    @PutMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Response<User>> update(HttpServletRequest request, @RequestBody User user, BindingResult result){
        Response<User> response = new Response<>();
        try{
            validateUpdateUser(user,result);
            if(result.hasErrors()){
                result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
                return ResponseEntity.badRequest().body(response);
            }
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            User userPersisted =  this.userService.createOrUpdate(user);
            response.setData(userPersisted);
        }catch (Exception e){
            response.getErrors().add(e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    private void validateCreateUser(User user,BindingResult result){
        if(user.getEmail() == null)
            result.addError(new ObjectError("User","Email no information"));

    }

    private void validateUpdateUser(User user,BindingResult result){
        if(user.getId() == null)
            result.addError(new ObjectError("User","Id no information"));

        if(user.getEmail() == null)
            result.addError(new ObjectError("User","Email no information"));
    }


    @GetMapping(value = "{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Response<User>> findById(@PathVariable("id") String id){
        Response<User> response = new Response<>();
        User user = this.userService.findById(id);
        if(user == null){
            return ResponseEntity.noContent().build();
        }
        response.setData(user);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping(value = "{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Response<String>> deleteById(@PathVariable("id") String id){
        Response<String> response = new Response<>();
        User user = this.userService.findById(id);
        if(user == null){
            response.getErrors().add("Register not found id: "+id);
            return ResponseEntity.badRequest().body(response);
        }
        try{
            this.userService.delete(id);
            response.setData("User deleted");
        }catch (Exception e){
            response.getErrors().add(e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "{page}/{count}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Response<Page<User>>> findAll(@PathVariable("page") int page,@PathVariable("count") int count){
        Response<Page<User>> response = new Response<>();
        Page<User> users = this.userService.findAll(page,count);
        response.setData(users);
        return ResponseEntity.ok(response);
    }


}
