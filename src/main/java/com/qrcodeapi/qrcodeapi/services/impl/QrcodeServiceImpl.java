package com.qrcodeapi.qrcodeapi.services.impl;

import com.qrcodeapi.qrcodeapi.responses.QrcodeResponse;
import com.qrcodeapi.qrcodeapi.services.QrcodeService;
import com.qrcodeapi.qrcodeapi.ultls.QrCode;
import com.qrcodeapi.qrcodeapi.ultls.QrCodeLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QrcodeServiceImpl implements QrcodeService {

    @Autowired
    private QrCodeLogger logger;

    @Override
    public QrcodeResponse generate(String content, int size) {

        long startTime = System.currentTimeMillis();
        long durationMs = 0;

        try {
            if (size < 1) {
                size = 1;
            }

            QrCode qrCode = new QrCode();
            String svg = qrCode.generateSvg(content, size, "");
            QrcodeResponse response = new QrcodeResponse();
            response.setSize(size);
            response.setContent(svg);
            return response;
        } catch (Exception e) {

            // log failure
            durationMs = System.currentTimeMillis() - startTime;
            logger.logFailure(content, e, durationMs);

            throw new RuntimeException(e);
        }finally {
            // log success
            durationMs = System.currentTimeMillis() - startTime;
            logger.logSuccess(content,durationMs);
        }

    }


}
