package com.fintrack.transactions;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/** Data-access layer for transactions. All queries are parameterised by Spring Data (no SQL injection). */
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByUserIdOrderByCreatedAtDesc(String userId);

    long deleteByUserId(String userId);
}
