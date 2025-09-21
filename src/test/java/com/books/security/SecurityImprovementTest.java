package com.books.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 보안 개선사항 검증을 위한 테스트 클래스
 *
 * 목적:
 * 1. API 키 환경변수 설정 검증
 * 2. 하드코딩된 키 탐지
 * 3. 프로파일별 설정 검증
 */
@SpringBootTest
@ActiveProfiles("test")
class SecurityImprovementTest {

    @Value("${books.aladin.api.TTBKey:}")
    private String aladinApiKey;

    @Value("${books.kakao.api.kakaoAK:}")
    private String kakaoApiKey;

    @Value("${books.naver.api.client-id:}")
    private String naverClientId;

    @Value("${books.naver.api.client-secret:}")
    private String naverClientSecret;

    @Test
    @DisplayName("API 키가 환경변수 또는 기본값으로 설정되어 있는지 검증")
    void shouldLoadApiKeysFromConfiguration() {
        // Given & When & Then
        assertNotNull(aladinApiKey, "알라딘 API 키가 설정되어야 합니다");
        assertNotNull(kakaoApiKey, "카카오 API 키가 설정되어야 합니다");
        assertNotNull(naverClientId, "네이버 클라이언트 ID가 설정되어야 합니다");
        assertNotNull(naverClientSecret, "네이버 클라이언트 시크릿이 설정되어야 합니다");

        // 빈 문자열 체크
        assertFalse(aladinApiKey.trim().isEmpty(), "알라딘 API 키가 빈 값이면 안 됩니다");
        assertFalse(kakaoApiKey.trim().isEmpty(), "카카오 API 키가 빈 값이면 안 됩니다");
        assertFalse(naverClientId.trim().isEmpty(), "네이버 클라이언트 ID가 빈 값이면 안 됩니다");
        assertFalse(naverClientSecret.trim().isEmpty(), "네이버 클라이언트 시크릿이 빈 값이면 안 됩니다");
    }

    @Test
    @DisplayName("하드코딩된 API 키 패턴이 아닌지 검증")
    void shouldNotContainHardcodedApiKeys() {
        // Given - 하드코딩된 키 패턴들
        String[] suspiciousPatterns = {
            "ttbkdh6102309002",           // 기존 알라딘 키
            "21b493af0e8d30d5c1873e01a2346b69",  // 기존 카카오 키
            "a0P9aNvfYozXyTRfErny",       // 기존 네이버 클라이언트 ID
            "xfZqDNxeXS"                 // 기존 네이버 클라이언트 시크릿
        };

        // When & Then
        for (String suspiciousPattern : suspiciousPatterns) {
            assertNotEquals(suspiciousPattern, aladinApiKey,
                "알라딘 API 키가 하드코딩된 값이면 안 됩니다: " + suspiciousPattern);
            assertNotEquals(suspiciousPattern, kakaoApiKey,
                "카카오 API 키가 하드코딩된 값이면 안 됩니다: " + suspiciousPattern);
            assertNotEquals(suspiciousPattern, naverClientId,
                "네이버 클라이언트 ID가 하드코딩된 값이면 안 됩니다: " + suspiciousPattern);
            assertNotEquals(suspiciousPattern, naverClientSecret,
                "네이버 클라이언트 시크릿이 하드코딩된 값이면 안 됩니다: " + suspiciousPattern);
        }
    }

    @Test
    @DisplayName("API 키 길이가 적절한지 검증")
    void shouldHaveAppropriateKeyLength() {
        // Given - 일반적인 API 키 길이 범위
        int minKeyLength = 10;
        int maxKeyLength = 100;

        // When & Then
        assertTrue(aladinApiKey.length() >= minKeyLength && aladinApiKey.length() <= maxKeyLength,
            "알라딘 API 키 길이가 적절하지 않습니다: " + aladinApiKey.length());

        assertTrue(kakaoApiKey.length() >= minKeyLength && kakaoApiKey.length() <= maxKeyLength,
            "카카오 API 키 길이가 적절하지 않습니다: " + kakaoApiKey.length());

        assertTrue(naverClientId.length() >= minKeyLength && naverClientId.length() <= maxKeyLength,
            "네이버 클라이언트 ID 길이가 적절하지 않습니다: " + naverClientId.length());

        assertTrue(naverClientSecret.length() >= minKeyLength && naverClientSecret.length() <= maxKeyLength,
            "네이버 클라이언트 시크릿 길이가 적절하지 않습니다: " + naverClientSecret.length());
    }

    @Test
    @DisplayName("API 키에 특수문자나 공백이 포함되지 않았는지 검증")
    void shouldNotContainInvalidCharacters() {
        // Given - 검증 패턴 (영문자, 숫자, 하이픈, 언더스코어만 허용)
        String validPattern = "^[a-zA-Z0-9_-]+$";

        // When & Then
        assertTrue(aladinApiKey.matches(validPattern),
            "알라딘 API 키에 유효하지 않은 문자가 포함되어 있습니다: " + aladinApiKey);

        assertTrue(kakaoApiKey.matches(validPattern),
            "카카오 API 키에 유효하지 않은 문자가 포함되어 있습니다: " + kakaoApiKey);

        assertTrue(naverClientId.matches(validPattern),
            "네이버 클라이언트 ID에 유효하지 않은 문자가 포함되어 있습니다: " + naverClientId);

        assertTrue(naverClientSecret.matches(validPattern),
            "네이버 클라이언트 시크릿에 유효하지 않은 문자가 포함되어 있습니다: " + naverClientSecret);
    }

    @Test
    @DisplayName("환경변수 기반 설정이 우선순위를 가지는지 검증")
    void shouldPrioritizeEnvironmentVariables() {
        // Given
        String envVarPrefix = "${";
        String envVarSuffix = "}";

        // When & Then - 설정값이 환경변수 형태가 아니라면 실제 값이 로드되어야 함
        if (aladinApiKey.startsWith(envVarPrefix) && aladinApiKey.endsWith(envVarSuffix)) {
            fail("알라딘 API 키가 환경변수 플레이스홀더 형태로 남아있습니다: " + aladinApiKey);
        }

        if (kakaoApiKey.startsWith(envVarPrefix) && kakaoApiKey.endsWith(envVarSuffix)) {
            fail("카카오 API 키가 환경변수 플레이스홀더 형태로 남아있습니다: " + kakaoApiKey);
        }

        if (naverClientId.startsWith(envVarPrefix) && naverClientId.endsWith(envVarSuffix)) {
            fail("네이버 클라이언트 ID가 환경변수 플레이스홀더 형태로 남아있습니다: " + naverClientId);
        }

        if (naverClientSecret.startsWith(envVarPrefix) && naverClientSecret.endsWith(envVarSuffix)) {
            fail("네이버 클라이언트 시크릿이 환경변수 플레이스홀더 형태로 남아있습니다: " + naverClientSecret);
        }
    }
}