package com.books.book.application

import org.springframework.stereotype.Service

@Service
class KotlinBookServiceImpl : KotlinBookService {

    override fun getBookNameByIsbn(isbn: String): String {

        return "클린코드"
    }
}