package com.qrcodeapi.qrcodeapi.payloads;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QrcodeRequest {
    private String content;
    private int size;
}
