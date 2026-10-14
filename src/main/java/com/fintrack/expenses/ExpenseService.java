package com.fintrack.expenses;

import com.fintrack.common.ForbiddenException;
import com.fintrack.transactions.TransactionService;
import com.fintrack.transactions.TransactionType;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Orchestrates expense creation and balance queries. Builds on the Transaction module. */
@Service
public class ExpenseService {

    private static final Logger log = LoggerFactory.getLogger(ExpenseService.class);

    private final SharedExpenseRepository repository;
    private final TransactionService transactionService;

    public ExpenseService(SharedExpenseRepository repository, TransactionService transactionService) {
        this.repository = repository;
        this.transactionService = transactionService;
    }

    /**
     * Creates a shared expense paid by {@code creatorId}. The payment is also recorded as a DEBIT
     * transaction on the creator's account, linking this module to the Transaction module.
     *
     * @throws com.fintrack.common.BusinessValidationException if split rules are violated
     */
    @Transactional
    public SharedExpense create(String creatorId, CreateExpenseRequest request) {
        Map<String, BigDecimal> shares = SplitCalculator.calculate(
                creatorId, request.totalAmount(), request.splitType(), request.participants());
        SharedExpense saved = repository.save(new SharedExpense(
                creatorId, request.description(), request.totalAmount(), request.splitType(), shares));
        transactionService.create(creatorId, request.totalAmount(), TransactionType.DEBIT,
                "Shared expense: " + request.description());
        log.info("event=expense_created expenseId={} creatorId={} participants={} split={}",
                saved.getId(), creatorId, shares.size(), request.splitType());
        return saved;
    }

    /**
     * Returns the pending balances of {@code requestedUserId} with every counterparty.
     *
     * @param callerId        authenticated user
     * @param requestedUserId whose balances are requested; must equal the caller
     * @throws ForbiddenException when the caller asks for someone else's balances
     */
    @Transactional(readOnly = true)
    public List<BalanceEntry> getBalances(String callerId, String requestedUserId) {
        if (!callerId.equals(requestedUserId)) {
            throw new ForbiddenException("You can only view your own balances");
        }
        List<Debt> debts = new ArrayList<>();
        for (SharedExpense e : repository.findAllInvolving(callerId)) {
            e.getShares().stream()
                    .filter(s -> !s.getParticipantId().equals(e.getCreatorId()))
                    .forEach(s -> debts.add(new Debt(s.getParticipantId(), e.getCreatorId(), s.getShareAmount())));
        }
        return BalanceCalculator.compute(callerId, debts);
    }
}
