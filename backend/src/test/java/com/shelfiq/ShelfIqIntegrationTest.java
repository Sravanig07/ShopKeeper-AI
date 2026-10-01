package com.shelfiq;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local")
public class ShelfIqIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testEndToEndPlatformFlow() throws Exception {
        // 1. Authenticate as Store Owner
        String loginJson = "{\"email\":\"owner@shelfiq.io\",\"password\":\"Password123!\"}";
        MvcResult loginResult = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode loginNode = objectMapper.readTree(loginResult.getResponse().getContentAsString());
        String token = loginNode.path("data").path("token").asText();
        assertNotNull(token);
        assertTrue(token.length() > 20);

        String authHeader = "Bearer " + token;

        // 2. Fetch Inventory Summary
        mockMvc.perform(get("/api/v1/inventory/summary")
                        .header("Authorization", authHeader))
                .andExpect(status().isOk());

        // 3. Fetch Today Sales Analytics
        mockMvc.perform(get("/api/v1/sales/analytics/today")
                        .header("Authorization", authHeader))
                .andExpect(status().isOk());

        // 4. Create Product & POS Checkout
        String createProductJson = """
                {
                    "sku": "TEST-PROD-01",
                    "barcode": "8901234567890",
                    "name": "Integration Test Product",
                    "sellingPrice": 40.00,
                    "costPrice": 30.00,
                    "minStock": 5,
                    "safetyStock": 10,
                    "initialStock": 50
                }
                """;
        MvcResult prodResult = mockMvc.perform(post("/api/v1/products")
                        .header("Authorization", authHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createProductJson))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode prodNode = objectMapper.readTree(prodResult.getResponse().getContentAsString());
        long productId = prodNode.path("data").path("id").asLong();

        String checkoutJson = String.format("""
                {
                    "items": [
                        { "productId": %d, "quantity": 1, "unitPrice": 40.00, "discountAmount": 0 }
                    ],
                    "paymentMethod": "UPI",
                    "customerName": "Test Customer",
                    "customerPhone": "+91 9999988888"
                }
                """, productId);

        mockMvc.perform(post("/api/v1/sales/checkout")
                        .header("Authorization", authHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(checkoutJson))
                .andExpect(status().isOk());

        // 5. AI Restock Recommendations
        mockMvc.perform(get("/api/v1/ai/restock-recommendations")
                        .header("Authorization", authHeader))
                .andExpect(status().isOk());

        // 6. AI Store Insights
        mockMvc.perform(get("/api/v1/ai/insights")
                        .header("Authorization", authHeader))
                .andExpect(status().isOk());

        // 7. AI Copilot Chat
        String chatJson = "{\"message\":\"What items are running low and need restock?\"}";
        mockMvc.perform(post("/api/v1/ai/chat")
                        .header("Authorization", authHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(chatJson))
                .andExpect(status().isOk());

        // 8. Purchase Orders List
        mockMvc.perform(get("/api/v1/purchase-orders")
                        .header("Authorization", authHeader))
                .andExpect(status().isOk());

        // 9. OCR Invoice Scan
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "sample-invoice.png",
                MediaType.IMAGE_PNG_VALUE,
                "Beverage World Dist. INVOICE #88392".getBytes()
        );
        mockMvc.perform(multipart("/api/v1/ocr/scan-invoice")
                        .file(mockFile)
                        .header("Authorization", authHeader))
                .andExpect(status().isOk());
    }
}
