package com.bombino.bank_service.service;

import com.bombino.bank_service.model.dto.CardDto;
import com.bombino.bank_service.model.dto.TransactionDto;
import com.bombino.bank_service.model.mapper.TransactionMapper;
import com.bombino.bank_service.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminService {
    private final TransactionRepository transactionRepository;
    private final CardServiceImpl cardService;
    private final TransactionMapper mapper;

    public List<TransactionDto> getAllTransaction() {
        return mapper.toDto(transactionRepository.findAll());
    }

    /**
     * <b>Получить все транзакции юзера по его картам</b>
     * <p>
     * Метод выдает список Транзакций (DTO)
     * сделанных пользователем по его картам
     */
    public List<TransactionDto> getAllTransactionsByUserId(UUID id) {
        List<UUID> cardIds = cardService.getCardsByUserId(id).stream()
                .map(CardDto::id)
                .toList();
        return transactionRepository.findTransactionsByCardIdIn(cardIds).stream()
                .map(mapper::toDto)
                .toList();

        /* функциональный подход, но тут несколько запросов  бд
        return cardService.getCardsByUserId(id).stream()
                .map(CardDto::id)
                .map(transactionRepository::findTransactionsByCardId)
                .flatMap(List::stream)
                .map(mapper::toDto)
                .toList();
         */
    }

    /**
     * <b>Получить все транзакции по карте</b>
     * <p>
     * Метод выдает список Транзакций (DTO)
     * сделанных по определенной карте
     */
    // todo: раскоментировать и доделать
//    public List<TransactionDto> getAllTransactionsByCardId(UUID id) {
//        return transactionRepository.findTransactionsByCardId(id).stream()
//                .map(mapper::toDto)
//                .toList();
//
//    }
//    public List<TransactionDto> getAllTransactionsByCardId(UUID id) {
//        return transactionRepository.findTransactionsByCardId(id).stream()
//                .map(mapper::toDto)
//                .toList();
//
//    }
}
