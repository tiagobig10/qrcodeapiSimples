package com.qrcodeapi.qrcodeapi.controllers;



import com.qrcodeapi.qrcodeapi.payloads.QrcodeRequest;
import com.qrcodeapi.qrcodeapi.services.QrcodeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping
public class QrCodeController {
    @Autowired
    private QrcodeService qrcodeService;

    // Endpoint
    @PostMapping("/generate-qrcode")
    public ResponseEntity<?> generate(@Valid @RequestBody(required = false) QrcodeRequest request) {
        return new ResponseEntity<>(qrcodeService.generate(request.getContent(), request.getSize()), HttpStatus.CREATED);
    }



}
