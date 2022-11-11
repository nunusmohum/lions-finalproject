package com.ll.ebookmarket.app.mail.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Builder
public class MailDto {
    private String address;
    private String title;
    private String content;
}
