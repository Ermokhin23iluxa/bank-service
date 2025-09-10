package com.bombino.bank_service.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Card {
    //private Long userId;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)// потом заменить на sequence
    private Long id;
    private BigDecimal money;
    @NotNull
    private String cardNumber;
    @NotNull
    @FutureOrPresent
    private LocalDate expirationDate;
    @NotNull
    private Integer cvv;

}
