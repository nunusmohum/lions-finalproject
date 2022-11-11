package com.ll.ebookmarket.app.email.repository;

import com.ll.ebookmarket.app.email.entity.SendEmailLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SendEmailLogRepository extends JpaRepository<SendEmailLog, Long> {
}
