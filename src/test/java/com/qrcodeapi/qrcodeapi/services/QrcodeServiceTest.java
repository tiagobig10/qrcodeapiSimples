package com.qrcodeapi.qrcodeapi.services;

import com.qrcodeapi.qrcodeapi.payloads.QrcodeRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class QrcodeServiceTest {
    // example de teste
    @Test
    @DisplayName("generateIsContentPresent")
    void generateIsContentPresent() {
        QrcodeRequest request = new QrcodeRequest();
        request.setContent("teste");
        assertFalse(request.getContent().isEmpty());
    }

    @Test
    @DisplayName("generateNotContentPresent")
    void generateNotContentPresent() {
        QrcodeRequest request = new QrcodeRequest();
        request.setContent("");
        assertTrue(request.getContent().isEmpty());
    }

    @Test
    @DisplayName("generateNullContentPresent")
    void generateNullContentPresent() {
        QrcodeRequest request = new QrcodeRequest();
        assertNull(request.getContent());
    }


}