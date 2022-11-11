package com.ll.ebookmarket.app.mail.service;

import com.ll.ebookmarket.app.mail.dto.MailDto;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender javaMailSender;

    public void sendMail(MailDto mailDto) {

        // 수신 대상을 담을 ArrayList 생성
        ArrayList<String> toUserList = new ArrayList<>();

        // 수신 대상 추가
        toUserList.add(mailDto.getAddress());

        // 수신 대상 개수
        int toUserSize = toUserList.size();

        // SimpleMailMessage (단순 텍스트 구성 메일 메시지 생성할 때 이용)
        SimpleMailMessage simpleMessage = new SimpleMailMessage();

        // 수신자 설정
        simpleMessage.setTo(toUserList.toArray(new String[toUserSize]));

        // 메일 제목
        simpleMessage.setSubject(mailDto.getTitle());

        // 메일 내용
        simpleMessage.setText(mailDto.getContent());

        // 메일 발송
        javaMailSender.send(simpleMessage);
    }
}
