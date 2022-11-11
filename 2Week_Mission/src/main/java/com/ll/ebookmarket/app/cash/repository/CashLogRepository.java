package com.ll.ebookmarket.app.cash.repository;

import com.ll.ebookmarket.app.cash.entity.CashLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CashLogRepository extends JpaRepository<CashLog, Long> {
}
