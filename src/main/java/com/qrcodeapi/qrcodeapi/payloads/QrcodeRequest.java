package com.qrcodeapi.qrcodeapi.payloads;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.validator.constraints.Length;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QrcodeRequest {

    @NotBlank(message = "content null")
    private String content;

    private int size;
}
