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

    @PostMapping("/{groupId}/add/{userId}")
    public String addToGroup(@PathVariable Long groupId, @PathVariable Long userId) {
        service.addUserToGroup(userId, groupId);
        return "User " + userId + " added to group " + groupId;
    }

    @DeleteMapping("/{groupId}/remove/{userId}")
    public String removeFromGroup(@PathVariable Long groupId, @PathVariable Long userId) {
        service.removeUserFromGroup(userId, groupId);
        return "User " + userId + " removed from group " + groupId;
    }
}
