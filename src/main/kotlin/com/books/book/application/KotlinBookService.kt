package com.books.book.application


interface KotlinBookService{

    fun getBookNameByIsbn(isbn: String): String
}