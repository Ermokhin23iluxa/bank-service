package com.bombino.bank_service.model.entity;

import com.bombino.bank_service.model.enums.OperationStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Operation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long senderId;
    private Long priemnikId;
    private BigDecimal sumOperation;
    private String valute;
    @Enumerated(EnumType.STRING)
    private OperationStatus status;
    private LocalDateTime startOperation;
    private LocalDateTime endOperation;

}
