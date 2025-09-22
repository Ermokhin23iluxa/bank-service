package com.bombino.bank_service.controller;

import com.bombino.bank_service.model.dto.CardDto;
import com.bombino.bank_service.service.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/cards")
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;

    @PostMapping
    public ResponseEntity<CardDto> createCard(){
        return ResponseEntity.status(HttpStatus.CREATED).body(cardService.createCard());
    }
    @DeleteMapping("/id")
    public ResponseEntity<Void> deleteCard(@PathVariable("id") UUID id){
        cardService.deleteCard(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
    @GetMapping("/id")
    public ResponseEntity<CardDto> getCardById(@PathVariable("id") UUID id){
        return ResponseEntity.status(HttpStatus.OK).body(cardService.getCardById(id));
    }
    @GetMapping()
    public ResponseEntity<List<CardDto>> getAllCards(){
        return ResponseEntity.status(HttpStatus.OK).body(cardService.getAllCards());
    }

}
