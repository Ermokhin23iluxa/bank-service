package com.bombino.bank_service.controller;

import com.bombino.bank_service.model.dto.CardDto;
import com.bombino.bank_service.service.CardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/cards")
@RequiredArgsConstructor
@Tag(name = "Card Management", description = "API для управления банковскими картами")
public class CardController {

    private final CardService cardService;

    @Operation(summary = "Создать новую карту", description = "Генерирует новую банковскую карту")
    @ApiResponse(responseCode = "201", description = "Карта успешно создана")
    @PostMapping("/{userId}")
    public ResponseEntity<CardDto> createCard(@PathVariable("userId") UUID userId){
        return ResponseEntity.status(HttpStatus.CREATED).body(cardService.createCard(userId));
    }
    @Operation(
            summary = "Удалить карту по ID",
            description = "Удаляет карту по её идентификатору"
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCard(
            @Parameter(description = "UUID карты", example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable("id") UUID id){
        cardService.deleteCard(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
    @Operation(
            summary = "Получить карту по ID",
            description = "Возвращает информацию о карте по её идентификатору"
    )
    @GetMapping("/{id}")
    public ResponseEntity<CardDto> getCardById(
            @Parameter(description = "UUID карты", example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable("id") UUID id){
        return ResponseEntity.status(HttpStatus.OK).body(cardService.getCardById(id));
    }
    @Operation(
            summary = "Вернуть список карт",
            description = "Возвращает информацию о картах"
    )
    @GetMapping()
    public ResponseEntity<List<CardDto>> getAllCards(){
        return ResponseEntity.status(HttpStatus.OK).body(cardService.getAllCards());
    }

}
