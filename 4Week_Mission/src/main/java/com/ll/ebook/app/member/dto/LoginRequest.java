package com.ll.ebook.app.member.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
public class LoginRequest {
    private String username;
    private String password;
}
