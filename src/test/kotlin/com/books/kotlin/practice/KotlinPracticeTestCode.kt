package com.books.kotlin.practice

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.util.*

class KotlinPracticeTestCode {

    @Test
    @DisplayName("문제 1: 컬렉션을 사용한 홀수/짝수 분리")
    fun test1() {
        val numbers = listOf(1, 4, 6, 7, 3, 8)
        
        val evenNumbers = numbers.filter { it % 2 == 0 }
        val oddNumbers = numbers.filter { it % 2 != 0 }
        
        println("짝수: $evenNumbers")
        println("홀수: $oddNumbers")
    }

    @Test
    @DisplayName("문제 2: 구구단 출력 (범위 함수 활용)")
    fun test2() {
        (2..9).forEach { dan ->
            (1..9).forEach { i ->
                println("$dan x $i = ${dan * i}")
            }
        }
    }

    @Test
    @DisplayName("문제 3: 피라미드 출력 (repeat 함수 활용)")
    fun test3() {
        repeat(5) { i ->
            val spaces = " ".repeat(5 - i - 1)
            val stars = "*".repeat(i + 1)
            println("$spaces$stars")
        }
    }

    @Test
    @DisplayName("문제 4: 특정 문자열 포함 필터링")
    fun test4() {
        val list = listOf("hello world", "hi there", "say hello", "goodbye")
        val keyword = "bye"
        
        list.filter { it.contains(keyword) }
            .forEach { println(it) }
    }

    @Test
    @DisplayName("문제 5: 짝수의 합 (함수형 프로그래밍)")
    fun test5() {
        val numbers = listOf(1, 2, 3, 4, 5, 6)
        val sum = numbers.filter { it % 2 == 0 }.sum()
        
        println(sum)
    }

    @Test
    @DisplayName("문제 6: 문자 개수 세기 (groupBy 사용)")
    fun test6() {
        val input = "hello world"
        val freq = input.groupBy { it }.mapValues { it.value.size }
        
        println(freq)
    }

    @Test
    @DisplayName("문제 7: 회문 확인 (확장 함수 활용)")
    fun test7() {
        val word = "level"
        val isPalindrome = word == word.reversed()
        
        println(isPalindrome)
    }

    @Test
    @DisplayName("문제 8: 피보나치 수열 (시퀀스 활용)")
    fun test8() {
        val n = 10
        
        val fibSequence = generateSequence(0 to 1) { it.second to (it.first + it.second) }
            .map { it.first }
            .take(n + 1)
            .last()
        
        println(fibSequence)
    }

    @Test
    @DisplayName("문제 8-1: 피보나치 수열 (메모이제이션 + 꼬리 재귀)")
    fun test8_v1() {
        val n = 10
        val memo = mutableMapOf<Int, Int>()
        
        fun fib(n: Int): Int = memo.getOrPut(n) {
            when (n) {
                0, 1 -> n
                else -> fib(n - 1) + fib(n - 2)
            }
        }
        
        val result = fib(n)
        println(result)
    }

    @Test
    @DisplayName("문제 9: 구간 합 구하기")
    fun test9() {
        val arr = intArrayOf(1, 2, 3, 4, 5)
        val start = 1
        val end = 3
        
        val sum = (start..end).sumOf { arr[it] }
        println(sum)
    }

    @Test
    @DisplayName("문제 10: 중복 제거 후 정렬")
    fun test10() {
        val list = listOf(5, 3, 2, 3, 5, 1)
        val result = list.distinct().sorted()
        
        println(result)
    }

    @Test
    @DisplayName("문제 11: 괄호 유효성 검사 (Stack 활용)")
    fun test11() {
        val s = "(()())"
        val stack = Stack<Char>()
        
        for (c in s) {
            when (c) {
                '(' -> stack.push(c)
                ')' -> {
                    if (stack.isEmpty()) {
                        println(false)
                        return
                    }
                    stack.pop()
                }
            }
        }
        
        println(stack.isEmpty())
    }

    @Test
    @DisplayName("문제 12: 두 수의 합 (Map 활용)")
    fun test12() {
        val nums = intArrayOf(2, 7, 11, 15)
        val target = 9
        val map = mutableMapOf<Int, Int>()
        
        nums.forEachIndexed { index, num ->
            val complement = target - num
            
            if (complement in map) {
                println("${map[complement]}, $index")
                return
            }
            
            map[num] = index
        }
    }

    @Test
    @DisplayName("문제 13: 단어 빈도수 정렬 (groupBy + sortedByDescending)")
    fun test13() {
        val sentence = "hello world hello hi"
        val countMap = sentence.split(" ")
            .groupBy { it }
            .mapValues { it.value.size }
        
        countMap.toList()
            .sortedByDescending { it.second }
            .forEach { (word, count) ->
                println("$word : $count")
            }
    }

    @Test
    @DisplayName("문제 14: 문자열 압축 (연속 문자 수 표시)")
    fun test14() {
        val input = "aaabbc"
        val result = buildString {
            var prev = input[0]
            var count = 1
            
            for (i in 1 until input.length) {
                if (input[i] == prev) {
                    count++
                } else {
                    append(prev).append(count)
                    prev = input[i]
                    count = 1
                }
            }
            append(prev).append(count)
        }
        
        println(result)
    }

    @Test
    @DisplayName("문제 15: 배열에서 동일 숫자와 개수를 찾아서 가장 많은 순서로 정렬하기")
    fun test15() {
        val nums = intArrayOf(4, 1, 4, 2, 5, 6, 7, 1, 4, 3, 2, 6, 9, 4, 1, 2)
        
        nums.toList()
            .groupBy { it }
            .mapValues { it.value.size }
            .toList()
            .sortedByDescending { it.second }
            .forEach { (number, count) ->
                println("숫자 : $number → ${count}번")
            }
    }

    @Test
    @DisplayName("문제 16: 입력으로 주어진 문자열에서 대문자 알파벳만 추출하여 리스트로 반환")
    fun test16() {
        val input = "AbcDefGHiJkL"
        
        val uppercases = input.filter { it.isUpperCase() }
        println(uppercases.toList())
    }

    @Test
    @DisplayName("문제 17: 반복되는 대문자와 소문자의 개수와 문자들 구하기")
    fun test17() {
        val input = "AbCDefGhAaBBccZZzz"
        
        val freqMap = input.groupBy { it }.mapValues { it.value.size }
        
        val repeatedUpper = freqMap
            .filter { it.key.isUpperCase() && it.value >= 2 }
            .keys.toList()
        
        val repeatedLower = freqMap
            .filter { it.key.isLowerCase() && it.value >= 2 }
            .keys.toList()
        
        println("반복되는 대문자 : ${repeatedUpper.size}개 → $repeatedUpper")
        println("반복되는 소문자 : ${repeatedLower.size}개 → $repeatedLower")
    }

    @Test
    @DisplayName("문제 18: 데이터 클래스와 함수형 프로그래밍 활용")
    fun test18() {
        data class Person(val name: String, val age: Int, val city: String)
        
        val people = listOf(
            Person("김철수", 25, "서울"),
            Person("이영희", 30, "부산"),
            Person("박민수", 25, "서울"),
            Person("최영수", 35, "대구")
        )
        
        // 서울 거주자 중 25세 이상 찾기
        val result = people
            .filter { it.city == "서울" && it.age >= 25 }
            .map { it.name }
        
        println("서울 거주 25세 이상: $result")
        
        // 도시별 평균 연령
        val avgAgeByCity = people
            .groupBy { it.city }
            .mapValues { entries -> entries.value.map { it.age }.average() }
        
        println("도시별 평균 연령: $avgAgeByCity")
    }

    @Test
    @DisplayName("문제 19: 확장 함수와 고차함수 활용")
    fun test19() {
        // 확장 함수 정의
        fun String.isPalindrome(): Boolean = this == this.reversed()
        fun <T> List<T>.second(): T? = if (size >= 2) this[1] else null
        
        // 고차함수 활용
        fun <T> List<T>.customFilter(predicate: (T) -> Boolean): List<T> {
            return this.filter(predicate)
        }
        
        val words = listOf("level", "hello", "radar", "world", "civic")
        
        val palindromes = words.customFilter { it.isPalindrome() }
        println("회문: $palindromes")
        
        val numbers = listOf(1, 2, 3, 4, 5)
        println("두 번째 요소: ${numbers.second()}")
    }

    @Test
    @DisplayName("문제 20: Result 패턴과 When 표현식 활용")
    fun test20() {
        // 간단한 Result 패턴 예제
        data class Success(val data: String)
        data class Error(val message: String)
        class Loading
        
        val loading = Loading()
        
        fun handleResult(result: Any) = when (result) {
            is Success -> "데이터: ${result.data}"
            is Error -> "오류: ${result.message}"
            is Loading -> "로딩 중..."
            else -> "알 수 없는 상태"
        }
        
        val results = listOf(
            Success("성공 데이터"),
            Error("네트워크 오류"),
            loading
        )
        
        results.forEach { result ->
            println(handleResult(result))
        }
    }
}