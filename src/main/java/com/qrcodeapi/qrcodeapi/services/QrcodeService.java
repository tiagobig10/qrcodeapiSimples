package com.qrcodeapi.qrcodeapi.services;

import com.qrcodeapi.qrcodeapi.responses.QrcodeResponse;

public interface QrcodeService {
    QrcodeResponse generate(String content, int size);


}
