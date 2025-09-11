package com.bombino.bank_service.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EncryptionService {
    private final TextEncryptor textEncryptor;

    public String encrypt(String plain) {
        return textEncryptor.encrypt(plain);
    }

    public String decrypt(String cipher) {
        return textEncryptor.decrypt(cipher);
    }
}
