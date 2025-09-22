package com.bombino.bank_service.service;


import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EncryptionService {
    private final TextEncryptor textEncryptor;

    public String encrypt(String plain) {
        if (plain == null) return null;
        return textEncryptor.encrypt(plain);
    }

    public String decrypt(String cipher) {
        if (cipher == null) return null;
        return textEncryptor.decrypt(cipher);
    }

    public String maskPan(String pan) {
        if (pan == null || pan.length() < 16) {
            return pan;
        }
        String panFirstFour = pan.substring(0, 4);
        String panLastFour = pan.substring(pan.length() - 4);
        return panFirstFour + " **** **** " + panLastFour;
    }
}
