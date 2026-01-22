package com.qrcodeapi.qrcodeapi.responses;

import lombok.AllArgsConstructor;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QrcodeResponse {

    public String content;
    public int[][] matrix;
    public Integer size;
}
