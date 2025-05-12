package com.books.java.practice;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Stack;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class JavaPracticeTests {

    @Test
    @DisplayName("문제 1 : 스트림을 사용한 홀수/짝수 분리")
    void test1() {

        List<Integer> numbers = Arrays.asList(1, 4, 6, 7, 3, 8);

        List<Integer> even = numbers.stream()
            .filter(n -> n % 2 == 0)
            .toList();

        List<Integer> odd = numbers.stream()
            .filter(n -> n % 2 != 0)
            .toList();

        System.out.println("짝수: " + even);
        System.out.println("홀수: " + odd);
    }

    @Test
    @DisplayName("문제 2 : 스트림을 이용한 구구단 출력")
    void test2() {

        IntStream.rangeClosed(2, 9).forEach(dan -> IntStream.rangeClosed(1, 9)
            .forEach(i -> System.out.printf("%d x %d = %d\n", dan, i, dan * i))
        );
    }

    @Test
    @DisplayName("문제 3 : 피라미드 출력")
    void test3() {

        String input = "5\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();

        for (int i = 1; i <= n; i++) {

            String spaces = " ".repeat(n - i);
            String stars = "*".repeat(i);

            System.out.println(spaces + stars);
        }
    }

    @Test
    @DisplayName("문제 4 : 특정 문자열 포함 필터링")
    void test4() {

        List<String> list = List.of("hello world", "hi there", "say hello", "goodbye");
        String keyword = "hello";

        list.stream()
            .filter(s -> s.contains(keyword))
            .forEach(System.out::println);
    }

    @Test
    @DisplayName("문제 5 : 짝수의 합 (기초 Stream)")
    void test5() {

        List<Integer> numbers = List.of(1, 2, 3, 4, 5, 6);
        int sum = numbers.stream().filter(n -> n % 2 == 0).mapToInt(n -> n).sum();

        System.out.println(sum); // 출력: 12
    }

    @Test
    @DisplayName("문제 6 : 문자 개수 세기 (Map 사용)")
    void test6() {

        String input = "hello world";
        Map<Character, Integer> freq = new HashMap<>();

        for (char c : input.toCharArray())
            freq.put(c, freq.getOrDefault(c, 0) + 1);

        System.out.println(freq);
    }

    @Test
    @DisplayName("문제 7 : 회문 확인 (Palindrome)")
    void test7() {

        String word = "level";
        boolean isPalindrome = word.contentEquals(new StringBuilder(word).reverse());

        System.out.println(isPalindrome); // true
    }

    @Test
    @DisplayName("문제 8 : 피보나치 수열 (재귀 없이)")
    void test8() {

        int n = 10;
        int a = 0, b = 1;

        for (int i = 2; i <= n; i++) {

            int tmp = a + b;
            a = b;
            b = tmp;
        }

        System.out.println(b);
    }

    @Test
    @DisplayName("문제 9 : 구간 합 구하기")
    void test9() {

        int[] arr = {1, 2, 3, 4, 5};
        int start = 1, end = 3;

        int sum = IntStream.rangeClosed(start, end)
            .map(i -> arr[i])
            .sum();

        System.out.println(sum);
    }

    @Test
    @DisplayName("문제 10 : 중복 제거 후 정렬")
    void test10() {

        List<Integer> list = Arrays.asList(5, 3, 2, 3, 5, 1);
        List<Integer> result = list.stream()
            .distinct()
            .sorted()
            .collect(Collectors.toList());

        System.out.println(result);
    }

    @Test
    @DisplayName("문제 11 : 괄호 유효성 검사 (스택)")
    void test11() {

        String s = "(()())";
        Stack<Character> stack = new Stack<>();

        for (char c : s.toCharArray()) {

            if (c == '(') {
                stack.push(c);

            } else if (c == ')' && !stack.isEmpty()) {
                stack.pop();

            } else {
                System.out.println(false);
                return;
            }
        }

        System.out.println(stack.isEmpty());
    }

    @Test
    @DisplayName("문제 12 : 두 수의 합 (HashMap)")
    void test12() {

        int[] nums = {2, 7, 11, 15};
        int target = 9;
        Map<Integer, Integer> map = new HashMap<>();

        for (int i = 0; i < nums.length; i++) {

            int complement = target - nums[i];

            if (map.containsKey(complement)) {
                System.out.println(map.get(complement) + ", " + i);
                break;
            }

            map.put(nums[i], i);
        }
    }

    @Test
    @DisplayName("문제 13 : 단어 빈도수 정렬 (Map + 정렬)")
    void test13() {

        String sentence = "hello world hello hi";
        Map<String, Integer> countMap = new HashMap<>();

        for (String word : sentence.split(" "))
            countMap.put(word, countMap.getOrDefault(word, 0) + 1);

        countMap.entrySet().stream()
            .sorted((e1, e2) -> e2.getValue() - e1.getValue())
            .forEach(e -> System.out.println(e.getKey() + " : " + e.getValue()));
    }

    @Test
    @DisplayName("문제 14 : 문자열 압축 (연속 문자 수 표시)")
    void test14() {

        String input = "aaabbc";
        StringBuilder sb = new StringBuilder();

        char prev = input.charAt(0);
        int count = 1;

        for (int i = 1; i < input.length(); i++) {

            if (input.charAt(i) == prev) {
                count++;

            } else {
                sb.append(prev).append(count);
                prev = input.charAt(i);
                count = 1;
            }
        }

        sb.append(prev).append(count);

        System.out.println(sb);
    }

    @Test
    @DisplayName("문제 15 : 배열에서 동일 숫자와 개수를 찾아서 가장 많은 순서로 정렬하기")
    void test15() {

        int[] nums = {4, 1, 4, 2, 5, 6, 7, 1, 4, 3, 2, 6, 9, 4, 1, 2};

        // 1. 숫자별 등장 횟수 집계
        Map<Integer, Long> frequencyMap = Arrays.stream(nums)
            .boxed()
            .collect(Collectors.groupingBy(n -> n, Collectors.counting()));

        // 2. 등장 횟수 기준으로 정렬 후 출력
        frequencyMap.entrySet().stream()
            .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
            .forEach(entry ->
                System.out.println("숫자 : " + entry.getKey() + " → " + entry.getValue() + "번"));
    }

    @Test
    @DisplayName("문제 16 : 입력으로 주어진 문자열에서 대문자 알파벳만 추출하여 리스트 또는 문자열로 반환")
    void test16() {

        String input = "AbcDefGHiJkL";

        List<Character> uppercases = input.chars()
            .mapToObj(c -> (char) c)
            .filter(Character::isUpperCase)
            .collect(Collectors.toList());

        System.out.println(uppercases);
    }

    @Test
    @DisplayName("\t반복되는 대문자의 개수와 어떤 문자들이 반복되었는지 반복되는 소문자의 개수와 어떤 문자들이 반복되었는지를 구하세요.")
    void test17() {

        String input = "AbCDefGhAaBBccZZzz";

        // 1. 문자별 빈도 계산
        Map<Character, Long> freqMap = input.chars()
            .mapToObj(c -> (char) c)
            .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        // 2. 대문자 중 2번 이상 등장한 문자
        List<Character> repeatedUpper = freqMap.entrySet().stream()
            .filter(e -> Character.isUpperCase(e.getKey()) && e.getValue() >= 2)
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());

        // 3. 소문자 중 2번 이상 등장한 문자
        List<Character> repeatedLower = freqMap.entrySet().stream()
            .filter(e -> Character.isLowerCase(e.getKey()) && e.getValue() >= 2)
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());

        // 4. 결과 출력
        System.out.println("반복되는 대문자: " + repeatedUpper.size() + "개 → " + repeatedUpper);
        System.out.println("반복되는 소문자: " + repeatedLower.size() + "개 → " + repeatedLower);
    }
}
