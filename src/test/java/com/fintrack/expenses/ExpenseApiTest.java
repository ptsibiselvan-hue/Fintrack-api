package com.fintrack.expenses;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class ExpenseApiTest {

    @Autowired MockMvc mvc;

    private void createExpense(String payer, String json) throws Exception {
        mvc.perform(post("/api/expenses").header("X-User-Id", payer)
                .contentType(MediaType.APPLICATION_JSON).content(json)).andExpect(status().isCreated());
    }

    @Test
    void test8_createEqualExpenseAndReadNetBalancesEndToEnd() throws Exception {
        createExpense("ann", """
            {"description":"Dinner","totalAmount":120.00,"splitType":"EQUAL",
             "participants":[{"userId":"ann"},{"userId":"ben"},{"userId":"cat"}]}""");
        createExpense("ben", """
            {"description":"Taxi","totalAmount":20.00,"splitType":"CUSTOM",
             "participants":[{"userId":"ann","amount":5.00},{"userId":"ben","amount":15.00}]}""");

        // ann owes ben 5 and ben owes ann 40 => net: ben owes ann 35
        mvc.perform(get("/api/users/ann/balances").header("X-User-Id", "ann"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.counterparty=='ben')].net").value(35.00))
                .andExpect(jsonPath("$[?(@.counterparty=='ben')].direction").value("THEY_OWE_YOU"));
    }

    @Test
    void test9_unauthorisedAccessToAnotherUsersBalances_isForbidden() throws Exception {
        mvc.perform(get("/api/users/ann/balances").header("X-User-Id", "mallory"))
                .andExpect(status().isForbidden());
        mvc.perform(get("/api/users/ann/balances")).andExpect(status().isUnauthorized());
    }

    @Test
    void test10_invalidCustomSplitReturns400() throws Exception {
        mvc.perform(post("/api/expenses").header("X-User-Id", "ann").contentType(MediaType.APPLICATION_JSON)
                .content("""
                {"description":"Rent","totalAmount":100.00,"splitType":"CUSTOM",
                 "participants":[{"userId":"ann","amount":40.00},{"userId":"ben","amount":40.00}]}"""))
                .andExpect(status().isBadRequest());
    }
}
