package com.shelfiq.store.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GeohashUtilTest {

    @Test
    void testEncodePrecision5And6() {
        // Bangalore coordinates: 12.978369, 77.640829
        String hash5 = GeohashUtil.encode(12.978369, 77.640829, 5);
        String hash6 = GeohashUtil.encode(12.978369, 77.640829, 6);

        assertNotNull(hash5);
        assertNotNull(hash6);
        assertEquals(5, hash5.length());
        assertEquals(6, hash6.length());
        assertTrue(hash6.startsWith(hash5)); // Precision 6 must start with precision 5 prefix
    }
}
