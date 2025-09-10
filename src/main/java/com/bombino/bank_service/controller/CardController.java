package com.bombino.bank_service.controller;

import com.bombino.bank_service.model.dto.CardDto;
import com.bombino.bank_service.service.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/BombinoBank/cards")
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;

    public ResponseEntity<CardDto> createCard(){
        return ResponseEntity.status(HttpStatus.CREATED).body(cardService.createCard());
    }
}
