package com.fintrack.expenses;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/** HTTP layer for expense splitting. Caller identity comes from the X-User-Id header. */
@RestController
@RequestMapping("/api")
public class ExpenseController {

    private final ExpenseService service;

    public ExpenseController(ExpenseService service) {
        this.service = service;
    }

    /** Creates a shared expense; the caller is the payer and must be a participant. */
    @PostMapping("/expenses")
    @ResponseStatus(HttpStatus.CREATED)
    public ExpenseResponse create(@RequestHeader("X-User-Id") String userId,
                                  @Valid @RequestBody CreateExpenseRequest request) {
        return ExpenseResponse.from(service.create(userId, request));
    }

    /** Lists the caller's pending balances: who they owe, who owes them, and net per person. */
    @GetMapping("/users/{userId}/balances")
    public List<BalanceEntry> balances(@RequestHeader("X-User-Id") String callerId,
                                       @PathVariable String userId) {
        return service.getBalances(callerId, userId);
    }
}
