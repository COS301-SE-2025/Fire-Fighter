package com.apex.firefighter.service;

import com.apex.firefighter.model.User;
import com.apex.firefighter.repository.UserRepository;
import com.apex.firefighter.service.auth.AuthenticationService;
import com.apex.firefighter.service.auth.AuthorizationService;
import com.apex.firefighter.service.role.RoleService;
import com.apex.firefighter.service.user.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {

    private final AuthenticationService authenticationService;
    private final AuthorizationService authorizationService;
    private final RoleService roleService;
    private final UserProfileService userProfileService;
    private final UserRepository userRepository;

    @Autowired
    public UserService(AuthenticationService authenticationService,
                      AuthorizationService authorizationService,
                      RoleService roleService,
                      UserProfileService userProfileService,
                      UserRepository userRepository) {
        this.authenticationService = authenticationService;
        this.authorizationService = authorizationService;
        this.roleService = roleService;
        this.userProfileService = userProfileService;
        this.userRepository = userRepository;
    }

    public Optional<User> findByFirebaseUid(String firebaseUid) {
        return userRepository.findByUserId(firebaseUid);
    }

    public User verifyOrCreateUser(String firebaseUid, String username, String email, String department) {
        return authenticationService.verifyOrCreateUser(firebaseUid, username, email, department);
    }

    public boolean isUserAuthorized(String firebaseUid) {
        return authorizationService.isUserAuthorized(firebaseUid);
    }

    public boolean hasRole(String firebaseUid, String roleName) {
        return authorizationService.hasRole(firebaseUid, roleName);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User authorizeUser(String firebaseUid, String authorizedBy) {
        return authorizationService.authorizeUser(firebaseUid, authorizedBy);
    }

    public User revokeUserAuthorization(String firebaseUid, String revokedBy) {
        return authorizationService.revokeUserAuthorization(firebaseUid, revokedBy);
    }

    public User assignRole(String firebaseUid, String roleName, String assignedBy) {
        return roleService.assignRole(firebaseUid, roleName, assignedBy);
    }
}
