package com.ll.ebook.app.myBook.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class MyBooksResponse {
    List<MyBookDto> myBooks;
}
