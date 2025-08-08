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

    @PutMapping("/{firebaseUid}/authorize")
    public ResponseEntity<User> authorizeUser(
            @PathVariable String firebaseUid,
            @RequestParam String authorizedBy) {
        try {
            User authorizedUser = userService.authorizeUser(firebaseUid, authorizedBy);
            return ResponseEntity.ok(authorizedUser);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{firebaseUid}/revoke")
    public ResponseEntity<User> revokeAuthorization(
            @PathVariable String firebaseUid,
            @RequestParam String revokedBy) {
        try {
            User revokedUser = userService.revokeUserAuthorization(firebaseUid, revokedBy);
            return ResponseEntity.ok(revokedUser);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{firebaseUid}/roles")
    public ResponseEntity<User> assignRole(
            @PathVariable String firebaseUid,
            @RequestParam String roleName,
            @RequestParam String assignedBy) {
        try {
            User updatedUser = userService.assignRole(firebaseUid, roleName, assignedBy);
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
