package com.apex.firefighter.service;

import com.apex.firefighter.model.ApiKey;
import com.apex.firefighter.model.User;


@Service
public class ApiKeyService {
    private final ApiKeyRepository apiKeyRepository;
    private final UserRepository userRepository;

    @Autowired
    public ApiKeyService(ApiKeyRepository apiKeyRepository, UserRepository userRepository) {
        this.apiKeyRepository = apiKeyRepository;
        this.userRepository = userRepository;
    }

    public ApiKey generateApiKeyForUser(String userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        String apiKeyValue = ApiKeyGenerator.generateApiKey();
        ApiKey apiKey = new ApiKey(apiKeyValue, user);
        return apiKeyRepository.save(apiKey);
    }
}
