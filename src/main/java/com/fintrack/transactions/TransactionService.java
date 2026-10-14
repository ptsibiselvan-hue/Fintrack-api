package com.fintrack.transactions;

import com.fintrack.common.ForbiddenException;
import com.fintrack.common.NotFoundException;
import java.math.BigDecimal;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Business logic for transactions. Every operation is scoped to the calling user. */
@Service
public class TransactionService {

    private static final Logger log = LoggerFactory.getLogger(TransactionService.class);
    private final TransactionRepository repository;

    public TransactionService(TransactionRepository repository) {
        this.repository = repository;
    }

    /**
     * Records a transaction for the given user.
     *
     * @param userId      owner of the transaction
     * @param amount      positive amount with at most 2 decimals
     * @param type        CREDIT or DEBIT
     * @param description free-text description
     * @return the saved transaction
     */
    @Transactional
    public Transaction create(String userId, BigDecimal amount, TransactionType type, String description) {
        Transaction saved = repository.save(new Transaction(userId, amount, type, description));
        log.info("event=transaction_created transactionId={} userId={} type={}", saved.getId(), userId, type);
        return saved;
    }

    /** Returns all transactions belonging to the user, newest first. */
    @Transactional(readOnly = true)
    public List<Transaction> getByUser(String userId) {
        return repository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    /**
     * Returns one transaction.
     *
     * @throws NotFoundException  if it does not exist
     * @throws ForbiddenException if it belongs to another user
     */
    @Transactional(readOnly = true)
    public Transaction getOwned(String userId, Long id) {
        Transaction tx = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Transaction " + id + " not found"));
        if (!tx.getUserId().equals(userId)) {
            throw new ForbiddenException("You cannot access this transaction");
        }
        return tx;
    }

    /**
     * Deletes ALL transactions of the calling user only (never other users' data).
     *
     * @return number of deleted rows
     */
    @Transactional
    public long deleteAllForUser(String userId) {
        long deleted = repository.deleteByUserId(userId);
        log.warn("event=transactions_deleted userId={} count={}", userId, deleted);
        return deleted;
    }
}
