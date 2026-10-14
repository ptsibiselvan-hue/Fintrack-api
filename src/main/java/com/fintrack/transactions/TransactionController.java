package com.fintrack.transactions;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * HTTP layer for transactions. The caller is identified by the X-User-Id header
 * (stand-in for a verified JWT subject in production).
 */
@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService service;

    public TransactionController(TransactionService service) {
        this.service = service;
    }

    /** Creates a transaction for the caller. */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponse create(@RequestHeader("X-User-Id") String userId,
                                      @Valid @RequestBody TransactionRequest request) {
        return TransactionResponse.from(
                service.create(userId, request.amount(), request.type(), request.description()));
    }

    /** Lists the caller's own transactions. */
    @GetMapping
    public List<TransactionResponse> list(@RequestHeader("X-User-Id") String userId) {
        return service.getByUser(userId).stream().map(TransactionResponse::from).toList();
    }

    /** Gets one of the caller's transactions (403 if owned by someone else). */
    @GetMapping("/{id}")
    public TransactionResponse get(@RequestHeader("X-User-Id") String userId, @PathVariable Long id) {
        return TransactionResponse.from(service.getOwned(userId, id));
    }

    /** Deletes all of the caller's own transactions. */
    @DeleteMapping
    public Map<String, Long> deleteAll(@RequestHeader("X-User-Id") String userId) {
        return Map.of("deleted", service.deleteAllForUser(userId));
    }
}
