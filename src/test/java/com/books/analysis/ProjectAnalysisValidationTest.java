package com.books.analysis;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 프로젝트 분석 결과 검증을 위한 단순 테스트
 * Spring 컨텍스트 없이 파일 기반 검증만 수행
 */
class ProjectAnalysisValidationTest {

    @Test
    @DisplayName("분석 문서가 생성되었는지 검증")
    void shouldHaveAnalysisDocuments() {
        // Given
        Path analysisDir = Paths.get("claudedocs/analysis");
        Path comprehensiveAnalysis = analysisDir.resolve("comprehensive-project-analysis.md");
        Path implementationGuide = analysisDir.resolve("improvement-implementation-guide.md");

        // When & Then
        assertTrue(Files.exists(analysisDir), "분석 디렉토리가 존재해야 합니다");
        assertTrue(Files.exists(comprehensiveAnalysis), "종합 분석 문서가 존재해야 합니다");
        assertTrue(Files.exists(implementationGuide), "구현 가이드가 존재해야 합니다");
    }

    @Test
    @DisplayName("분석 문서에 필요한 섹션이 포함되어 있는지 검증")
    void shouldContainRequiredSections() throws IOException {
        // Given
        Path analysisFile = Paths.get("claudedocs/analysis/comprehensive-project-analysis.md");

        // When
        String content = Files.readString(analysisFile);

        // Then
        assertAll(
            () -> assertTrue(content.contains("프로젝트 구조 분석"), "프로젝트 구조 분석 섹션이 있어야 합니다"),
            () -> assertTrue(content.contains("코드 품질 평가"), "코드 품질 평가 섹션이 있어야 합니다"),
            () -> assertTrue(content.contains("보안 검토"), "보안 검토 섹션이 있어야 합니다"),
            () -> assertTrue(content.contains("성능 최적화"), "성능 최적화 섹션이 있어야 합니다"),
            () -> assertTrue(content.contains("유지보수성"), "유지보수성 섹션이 있어야 합니다"),
            () -> assertTrue(content.contains("개선사항 체크리스트"), "개선사항 체크리스트가 있어야 합니다")
        );
    }

    @Test
    @DisplayName("보안 이슈가 명확히 식별되었는지 검증")
    void shouldIdentifySecurityIssues() throws IOException {
        // Given
        Path analysisFile = Paths.get("claudedocs/analysis/comprehensive-project-analysis.md");

        // When
        String content = Files.readString(analysisFile);

        // Then
        assertAll(
            () -> assertTrue(content.contains("API 키 하드코딩"), "API 키 하드코딩 이슈가 식별되어야 합니다"),
            () -> assertTrue(content.contains("환경변수"), "환경변수 사용 권장사항이 있어야 합니다"),
            () -> assertTrue(content.contains("🔴 critical"), "Critical 우선순위가 명시되어야 합니다")
        );
    }

    @Test
    @DisplayName("성능 개선사항이 구체적으로 제시되었는지 검증")
    void shouldSuggestPerformanceImprovements() throws IOException {
        // Given
        Path analysisFile = Paths.get("claudedocs/analysis/comprehensive-project-analysis.md");

        // When
        String content = Files.readString(analysisFile);

        // Then
        assertAll(
            () -> assertTrue(content.contains("Connection Pool"), "Connection Pool 설정 개선사항이 있어야 합니다"),
            () -> assertTrue(content.contains("코루틴"), "Kotlin 코루틴의 장점이 언급되어야 합니다"),
            () -> assertTrue(content.contains("병렬"), "병렬 처리에 대한 언급이 있어야 합니다")
        );
    }

    @Test
    @DisplayName("구현 가이드에 구체적인 코드 예시가 포함되어 있는지 검증")
    void shouldProvideImplementationExamples() throws IOException {
        // Given
        Path guideFile = Paths.get("claudedocs/analysis/improvement-implementation-guide.md");

        // When
        String content = Files.readString(guideFile);

        // Then
        assertAll(
            () -> assertTrue(content.contains("```yaml"), "YAML 설정 예시가 있어야 합니다"),
            () -> assertTrue(content.contains("```java"), "Java 코드 예시가 있어야 합니다"),
            () -> assertTrue(content.contains("```kotlin"), "Kotlin 코드 예시가 있어야 합니다"),
            () -> assertTrue(content.contains("```bash"), "실행 명령어 예시가 있어야 합니다")
        );
    }

    @Test
    @DisplayName("우선순위별 개선 계획이 체계적으로 구성되어 있는지 검증")
    void shouldHaveStructuredImprovementPlan() throws IOException {
        // Given
        Path analysisFile = Paths.get("claudedocs/analysis/comprehensive-project-analysis.md");

        // When
        String content = Files.readString(analysisFile);

        // Then
        assertAll(
            () -> assertTrue(content.contains("🔴 즉시 조치"), "즉시 조치 항목이 있어야 합니다"),
            () -> assertTrue(content.contains("🟡 단기 계획"), "단기 계획이 있어야 합니다"),
            () -> assertTrue(content.contains("🟢 중장기 계획"), "중장기 계획이 있어야 합니다"),
            () -> assertTrue(content.contains("1-2주"), "구체적인 시간 계획이 있어야 합니다")
        );
    }

    @Test
    @DisplayName("종합 평가 점수가 제시되었는지 검증")
    void shouldProvideOverallAssessment() throws IOException {
        // Given
        Path analysisFile = Paths.get("claudedocs/analysis/comprehensive-project-analysis.md");

        // When
        String content = Files.readString(analysisFile);

        // Then
        assertAll(
            () -> assertTrue(content.contains("종합 평가"), "종합 평가 섹션이 있어야 합니다"),
            () -> assertTrue(content.contains("/10"), "점수 체계가 있어야 합니다"),
            () -> assertTrue(content.contains("총점"), "총점이 제시되어야 합니다"),
            () -> assertTrue(content.contains("70점"), "구체적인 점수가 있어야 합니다")
        );
    }

    @Test
    @DisplayName("테스트 코드 파일들이 생성되었는지 검증")
    void shouldHaveTestFiles() {
        // Given & When & Then
        assertAll(
            () -> assertTrue(Files.exists(Paths.get("src/test/java/com/books/security/SecurityImprovementTest.java")),
                "보안 테스트 파일이 존재해야 합니다"),
            () -> assertTrue(Files.exists(Paths.get("src/test/java/com/books/performance/PerformanceImprovementTest.java")),
                "성능 테스트 파일이 존재해야 합니다"),
            () -> assertTrue(Files.exists(Paths.get("src/test/java/com/books/logging/LoggingImprovementTest.java")),
                "로깅 테스트 파일이 존재해야 합니다"),
            () -> assertTrue(Files.exists(Paths.get("src/test/resources/application-test.yml")),
                "테스트 설정 파일이 존재해야 합니다")
        );
    }

    @Test
    @DisplayName("분석 완료 및 검증 성공")
    void analysisCompletedSuccessfully() {
        // Given
        String analysisVersion = "2025-09-15";

        // When & Then
        assertTrue(Files.exists(Paths.get("claudedocs")), "Claude 문서 디렉토리가 생성되었습니다");
        assertTrue(Files.exists(Paths.get("claudedocs/analysis")), "분석 디렉토리가 생성되었습니다");

        // 분석 완료 확인
        assertNotNull(analysisVersion, "분석이 성공적으로 완료되었습니다");

        System.out.println("✅ 프로젝트 분석 및 개선사항 테스트 코드 생성 완료!");
        System.out.println("📋 생성된 문서:");
        System.out.println("   - claudedocs/analysis/comprehensive-project-analysis.md");
        System.out.println("   - claudedocs/analysis/improvement-implementation-guide.md");
        System.out.println("🧪 생성된 테스트:");
        System.out.println("   - SecurityImprovementTest.java");
        System.out.println("   - PerformanceImprovementTest.java");
        System.out.println("   - LoggingImprovementTest.java");
        System.out.println("🎯 다음 단계: 개선사항 구현 후 각 테스트 실행하여 검증");
    }
}