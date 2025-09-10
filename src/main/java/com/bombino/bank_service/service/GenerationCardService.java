package com.bombino.bank_service.service;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Random;

@Service
public class GenerationCardService {
    public Integer generationCVV(){
        Random random = new Random();
        return random.nextInt(1000);
    }
    public String generationCardNumber(){
        Random random = new Random();
        int zeroPart = 55;
        int firstPart = random.nextInt(100000000);
        int secondPart = random.nextInt(1000000);

        return Integer.toString(zeroPart) + Integer.toString(firstPart) + Integer.toString(secondPart);
    }
    public LocalDate generationDate(LocalDate date){
        return date.plusYears(3);
    }

}
