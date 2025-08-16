package com.apex.firefighter.controller;

import org.springframework.web.bind.annotation.*;

import com.apex.firefighter.service.DolibarrUserGroupService;

@RestController
@RequestMapping("/api/dolibarr/groups")
public class DolibarrUserGroupController {

    private final DolibarrUserGroupService service;

    public DolibarrUserGroupController(DolibarrUserGroupService service) {
        this.service = service;
    }

    @PostMapping("/add/{userId}")
    public String addToGroup(@PathVariable Long userId) {
        service.addUserToGroup(userId);
        return "User " + userId + " added to group Firefighters";
    }

    @DeleteMapping("/remove/{userId}")
    public String removeFromGroup(@PathVariable Long userId) {
        service.removeUserFromGroup(userId);
        return "User " + userId + " removed from group Firefighters";
    }
}
