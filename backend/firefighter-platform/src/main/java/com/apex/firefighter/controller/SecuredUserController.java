package com.apex.firefighter.controller;

import com.apex.firefighter.model.User;
import com.apex.firefighter.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/endpoints/users")
public class SecuredUserController {

    private final UserService userService;

    @Autowired
    public SecuredUserController(UserService userService) {
        this.userService = userService;
    }
}
