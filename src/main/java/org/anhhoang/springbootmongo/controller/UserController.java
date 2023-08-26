package org.anhhoang.springbootmongo.controller;

import lombok.RequiredArgsConstructor;
import org.anhhoang.springbootmongo.entity.User;
import org.anhhoang.springbootmongo.repository.UserRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "api")
public class UserController {

    private final UserRepository userRepo;

    @PostMapping("/addUser")
    public User addUser(@RequestBody User user) {
        return userRepo.save(user);
    }

    @GetMapping("/getAllUsers")
    public List<User> getAllUser(){
        return userRepo.findAll();
    }

    @GetMapping("/health")
    public String health(){
        return "OK";
    }
}
