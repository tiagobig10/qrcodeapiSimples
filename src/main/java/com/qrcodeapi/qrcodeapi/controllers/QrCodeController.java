package com.qrcodeapi.qrcodeapi.controllers;



import com.qrcodeapi.qrcodeapi.payloads.QrcodeRequest;
import com.qrcodeapi.qrcodeapi.services.QrcodeService;
import com.qrcodeapi.qrcodeapi.ultls.MatrixToSvg;
import com.qrcodeapi.qrcodeapi.ultls.QrCode;
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

    @PostMapping("/generate-qrcode")
    @CrossOrigin("*")
    public ResponseEntity<?> generate(@Valid @RequestBody(required = false) QrcodeRequest request) {
        return new ResponseEntity<>(qrcodeService.generate(request.getContent(), request.getSize()), HttpStatus.CREATED);
    }


}
