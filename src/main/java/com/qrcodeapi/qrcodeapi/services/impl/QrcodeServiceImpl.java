package com.qrcodeapi.qrcodeapi.services.impl;

import com.qrcodeapi.qrcodeapi.responses.QrcodeResponse;
import com.qrcodeapi.qrcodeapi.services.QrcodeService;
import com.qrcodeapi.qrcodeapi.ultls.QrCode;
import org.springframework.stereotype.Service;

@Service
public class QrcodeServiceImpl implements QrcodeService {
    @Override
    public QrcodeResponse generate(String content, int size) {

        QrCode qrCode = new QrCode();
        String svg = qrCode.generateSvg(content, size, "");
        QrcodeResponse response = new QrcodeResponse();
        response.setSize(size);
        response.setContent(svg);
        return response;
    }


}
