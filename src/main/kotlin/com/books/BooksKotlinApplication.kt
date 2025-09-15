package com.books

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class BooksKotlinApplication

fun main(args: Array<String>) {
    runApplication<BooksKotlinApplication>(*args)

    var a = 10
    changePrimitive(a)
    println("a: $a") // Q1. 출력 값은?

    val p = KotlinPerson("홍길동")
    changeReference(p)
    println("p.name: ${p.name}") // Q2. 출력 값은?

    reassignReference(p)
    println("p.name after reassign: ${p.name}") // Q3. 출력 값은?
}

fun changePrimitive(value: Int) {
    // value = 999 // Kotlin에서는 파라미터가 val이므로 불가능
    println("Inside changePrimitive: $value")
}

fun changeReference(person: KotlinPerson) {
    person.name = "이순신"
}

fun reassignReference(person: KotlinPerson) {
    // person = KotlinPerson("세종대왕") // Kotlin에서는 파라미터가 val이므로 불가능
    println("Inside reassignReference: ${person.name}")
}

data class KotlinPerson(var name: String)