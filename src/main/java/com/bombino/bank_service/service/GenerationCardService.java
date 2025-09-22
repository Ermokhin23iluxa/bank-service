package com.bombino.bank_service.service;

import com.bombino.bank_service.configuration.CryptoUtils;
import com.bombino.bank_service.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class GenerationCardService {

    private final CardRepository cardRepository;

    public String generationCVV(){
        log.debug("Генерация CVV");
        Random random = new Random();
        return Integer.toString(random.nextInt(1000));
    }

    public String generateUniquePan(){
        log.info("Генерация уникального pan");
        int attempts = 0;
        while (attempts++ < 100) {
            String pan = PanGenerator.generatePanWithLuhn();
            String panHash = CryptoUtils.sha256Hex(pan);
            if (!cardRepository.existsByPanHash(panHash)) {
                log.info("Успешная генерация PAN");
                return pan;
            }
        }
        log.warn("Ошибка генерации PAN");
        throw new RuntimeException("Не удалось сгенерировать уникальный PAN");
    }
    public LocalDate generationDate(LocalDate date){
        log.info("Генерация даты");
        return date.plusYears(3);
    }

}
