package com.books.kotlin.practice

import org.junit.jupiter.api.Test

class KotlinPracticeTestCode {

    private val numbers: List<Int> = listOf(1, 2, 3, 4, 5)
    private val evenNumber = numbers.filter { it % 2 == 0 }


    @Test
    fun test() {

        var test = "aaa"
        println(evenNumber)
    }
}