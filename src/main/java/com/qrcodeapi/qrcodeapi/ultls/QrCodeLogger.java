package com.qrcodeapi.qrcodeapi.ultls;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class QrCodeLogger {

    private static final Logger logger = LoggerFactory.getLogger(QrCodeLogger.class);

    public void logSuccess(String content, long durationMs) {
        logger.info("QRCODE_GENERATION_SUCCESS - Content: '{}', Duration: {}ms", content, durationMs);
    }

    public void logFailure(String content, Throwable e, long durationMs) {
        logger.error("QRCODE_GENERATION_FAILURE - Content: '{}', Duration: {}ms", content, durationMs, e);
    }

}
