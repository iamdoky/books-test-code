package com.books;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BooksApplication {

	public static void main(String[] args) {
		SpringApplication.run(BooksApplication.class, args);

		int a = 10;
		changePrimitive(a);
		System.out.println("a: " + a); // Q1. 출력 값은?

		Person p = new Person("홍길동");
		changeReference(p);
		System.out.println("p.name: " + p.name); // Q2. 출력 값은?

		reassignReference(p);
		System.out.println("p.name after reassign: " + p.name); // Q3. 출력 값은?
	}


	static void changePrimitive(int value) {
		value = 999;
	}

	static void changeReference(Person person) {
		person.name = "이순신";
	}

	static void reassignReference(Person person) {
		person = new Person("세종대왕");
	}
}

class Person {
	String name;

	public Person(String name) {
		this.name = name;
	}
}
