package com.fintrack.transactions;

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
class TransactionApiTest {

    @Autowired MockMvc mvc;

    private static final String BODY = "{\"amount\":25.50,\"type\":\"DEBIT\",\"description\":\"Coffee\"}";

    @Test
    void test11_userCannotReadAnotherUsersTransaction() throws Exception {
        String res = mvc.perform(post("/api/transactions").header("X-User-Id", "u1")
                .contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        String id = res.replaceAll(".*\"id\":(\\d+).*", "$1");

        mvc.perform(get("/api/transactions/" + id).header("X-User-Id", "u1")).andExpect(status().isOk());
        mvc.perform(get("/api/transactions/" + id).header("X-User-Id", "u2")).andExpect(status().isForbidden());
    }

    @Test
    void test12_invalidAmountRejected_andDeleteAllOnlyAffectsCaller() throws Exception {
        mvc.perform(post("/api/transactions").header("X-User-Id", "u3").contentType(MediaType.APPLICATION_JSON)
                .content("{\"amount\":-5,\"type\":\"DEBIT\",\"description\":\"x\"}")).andExpect(status().isBadRequest());

        mvc.perform(post("/api/transactions").header("X-User-Id", "u4").contentType(MediaType.APPLICATION_JSON).content(BODY));
        mvc.perform(post("/api/transactions").header("X-User-Id", "u5").contentType(MediaType.APPLICATION_JSON).content(BODY));
        mvc.perform(delete("/api/transactions").header("X-User-Id", "u4")).andExpect(jsonPath("$.deleted").value(1));
        mvc.perform(get("/api/transactions").header("X-User-Id", "u5")).andExpect(jsonPath("$.length()").value(1));
    }
}
