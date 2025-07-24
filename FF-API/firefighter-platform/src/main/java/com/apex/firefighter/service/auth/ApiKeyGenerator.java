package com.apex.firefighter.service;

import java.security.SecureRandom;
import java.util.Base64;

public class ApiKeyGenerator {
    private static final int API_KEY_BYTE_LENGTH = 32;

    public static String generateApiKey() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[API_KEY_BYTE_LENGTH];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
} 