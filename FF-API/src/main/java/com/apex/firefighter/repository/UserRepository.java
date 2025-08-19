package com.apex.firefighter.repository;

import com.apex.firefighter.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    
    // Find user by Firebase UID (primary key)
    Optional<User> findByUserId(String userId);
    
    // Find user by username
    Optional<User> findByUsername(String username);
    
    // Find user by email
    Optional<User> findByEmail(String email);
    
    // Find authorized users only
    List<User> findByIsAuthorizedTrue();
    
    // Find users by department
    List<User> findByDepartment(String department);
    
    // Find users by authorization status
    List<User> findByIsAuthorized(Boolean isAuthorized);
    
    // Check if user exists by Firebase UID
    boolean existsByUserId(String userId);
    
    // Check if user exists by email
    boolean existsByEmail(String email);
    
    // Check if user exists by username
    boolean existsByUsername(String username);
    
    // Simple role queries using the role column
    List<User> findByRole(String role);
    
    // Find authorized users with specific role
    List<User> findByRoleAndIsAuthorizedTrue(String role);
    
    // Find admin users only
    List<User> findByIsAdminTrue();
    
    // Find users by admin status
    List<User> findByIsAdmin(Boolean isAdmin);
    
    // Find authorized admin users
    List<User> findByIsAdminTrueAndIsAuthorizedTrue();
}
