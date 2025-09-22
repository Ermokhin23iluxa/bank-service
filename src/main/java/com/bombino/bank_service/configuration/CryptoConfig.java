package com.bombino.bank_service.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;

@Configuration
public class CryptoConfig {
    @Bean
    public TextEncryptor textEncryptor(
            @Value("${encrypt.secret-key}")
            String info,
            @Value("${encrypt.salt}")
            String salt
    ) {
        return Encryptors.text(info, salt);
    }
}
