package com.apex.firefighter.repository;

import com.apex.firefighter.model.ApiKey;
import com.apex.firefighter.model.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface ApiKeyRepository extends JpaRepository<ApiKey, Long> {
    Optional<ApiKey> findByApiKey(String apiKey);
    List<ApiKey> findByUser(User user);
}
