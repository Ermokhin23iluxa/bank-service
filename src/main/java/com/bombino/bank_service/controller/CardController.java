package com.bombino.bank_service.controller;

import com.bombino.bank_service.model.dto.CardDto;
import com.bombino.bank_service.service.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/BombinoBank/cards")
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;

    @PostMapping
    public ResponseEntity<CardDto> createCard(){
        return ResponseEntity.status(HttpStatus.CREATED).body(cardService.createCard());
    }
    @DeleteMapping("/id/del")
    public ResponseEntity<Void> deleteCard(@PathVariable("id")Long id){
        cardService.deleteCard(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
    @GetMapping("/id")
    public ResponseEntity<CardDto> getCardById(@PathVariable("id") Long id){

        return ResponseEntity.status(HttpStatus.OK).body(cardService.getCardById(id));
    }
    @GetMapping()
    public ResponseEntity<List<CardDto>> getCardById(){

        return ResponseEntity.status(HttpStatus.OK).body(cardService.getAllCards());
    }

}
