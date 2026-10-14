package com.fintrack.expenses;

import jakarta.persistence.*;
import java.math.BigDecimal;

/** One participant's share of a shared expense. */
@Entity
@Table(name = "expense_shares", indexes = @Index(name = "idx_share_participant", columnList = "participantId"))
public class ExpenseShare {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "expense_id")
    private SharedExpense expense;

    @Column(nullable = false)
    private String participantId;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal shareAmount;

    protected ExpenseShare() { }

    ExpenseShare(SharedExpense expense, String participantId, BigDecimal shareAmount) {
        this.expense = expense;
        this.participantId = participantId;
        this.shareAmount = shareAmount;
    }

    public Long getId() { return id; }
    public SharedExpense getExpense() { return expense; }
    public String getParticipantId() { return participantId; }
    public BigDecimal getShareAmount() { return shareAmount; }
}
