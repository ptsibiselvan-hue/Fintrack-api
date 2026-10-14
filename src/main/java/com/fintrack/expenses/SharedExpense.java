package com.fintrack.expenses;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** A shared expense paid by its creator and split among participants. */
@Entity
@Table(name = "shared_expenses", indexes = @Index(name = "idx_expense_creator", columnList = "creatorId"))
public class SharedExpense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, updatable = false)
    private String creatorId;

    @Column(nullable = false, length = 255)
    private String description;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SplitType splitType;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @OneToMany(mappedBy = "expense", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ExpenseShare> shares = new ArrayList<>();

    protected SharedExpense() { }

    public SharedExpense(String creatorId, String description, BigDecimal totalAmount,
                         SplitType splitType, Map<String, BigDecimal> shareByParticipant) {
        this.creatorId = creatorId;
        this.description = description;
        this.totalAmount = totalAmount;
        this.splitType = splitType;
        this.createdAt = Instant.now();
        shareByParticipant.forEach((p, amt) -> shares.add(new ExpenseShare(this, p, amt)));
    }

    public Long getId() { return id; }
    public String getCreatorId() { return creatorId; }
    public String getDescription() { return description; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public SplitType getSplitType() { return splitType; }
    public Instant getCreatedAt() { return createdAt; }
    public List<ExpenseShare> getShares() { return shares; }

    /** Shares in insertion order as participant -> amount. */
    public Map<String, BigDecimal> shareMap() {
        Map<String, BigDecimal> m = new LinkedHashMap<>();
        shares.forEach(s -> m.put(s.getParticipantId(), s.getShareAmount()));
        return m;
    }
}
