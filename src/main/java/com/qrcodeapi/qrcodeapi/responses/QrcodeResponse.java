package com.qrcodeapi.qrcodeapi.responses;

import lombok.AllArgsConstructor;

import lombok.NoArgsConstructor;


@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class QrcodeResponse {
    public String content;
    public Integer size;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }
}
