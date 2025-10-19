package com.qrcodeapi.qrcodeapi.payloads;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
public class QrcodeRequest {

    private String content;
    private int size;

    public void setContent(String content) {
        this.content = content;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getContent() {
        return content;
    }

    public int getSize() {
        return size;
    }
}
