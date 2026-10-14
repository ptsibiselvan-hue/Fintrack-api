package com.fintrack.expenses;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/** Data-access layer for shared expenses (parameterised JPQL only). */
public interface SharedExpenseRepository extends JpaRepository<SharedExpense, Long> {

    /** All expenses the user created or participates in, with every share loaded. */
    @Query("""
           select distinct e from SharedExpense e left join fetch e.shares
           where e.creatorId = :userId
              or e.id in (select s.expense.id from ExpenseShare s where s.participantId = :userId)
           """)
    List<SharedExpense> findAllInvolving(@Param("userId") String userId);
}
