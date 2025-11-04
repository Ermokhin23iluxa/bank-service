package com.bombino.bank_service.controller;

import com.bombino.bank_service.model.dto.CardDto;
import com.bombino.bank_service.model.dto.TransactionDto;
import com.bombino.bank_service.service.AdminService;
import com.bombino.bank_service.service.CardService;
import com.bombino.bank_service.service.CardServiceImpl;
import com.bombino.bank_service.service.CardTransactionService;
import com.bombino.bank_service.transfer.TransferService;
import com.bombino.bank_service.user.UserDto;
import com.bombino.bank_service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/admin")
@RequiredArgsConstructor
public class AdminPanelController {
    private final UserService userService;
    private final CardServiceImpl cardService;
    private final AdminService adminService;


    @GetMapping("/cards/{userId}")
    public ResponseEntity<List<CardDto>> getCardsForUserId(@PathVariable("userId") UUID id){
        return ResponseEntity.ok().body(cardService.getCardsByUserId(id));
    }
    @GetMapping("/users")
    public ResponseEntity<List<UserDto>> getAllUsers(){
        return ResponseEntity.status(HttpStatus.OK).body(userService.findAllUsers());
    }
    @GetMapping("/transactions")
    public ResponseEntity<List<TransactionDto>> getAllTransactions(){
        return ResponseEntity.status(HttpStatus.OK).body(adminService.getAllTransaction());
    }
    @GetMapping("/transactions/{userId}")
    public ResponseEntity<List<TransactionDto>> getAllTransactionsByUserId(
            @PathVariable("userId") UUID id){
        return ResponseEntity.status(HttpStatus.OK).body(adminService.getAllTransactionsByUserId(id));
    }
    // todo: доделать пагинацию
//    @GetMapping("/transactions/{cardId}")
//    public ResponseEntity<List<TransactionDto>> getAllTransactionsByCardId(
//            @PathVariable("cardId") UUID id){
//        return ResponseEntity.status(HttpStatus.OK).body(adminService.getAllTransactionsByCardId(id));
//    }

//    @GetMapping("/transactions/{cardId}/{limit}")
//    public ResponseEntity<List<TransactionDto>> getTransactionsByCardId(
//            @PathVariable("cardId") UUID id,
//            @PathVariable("limit") int limit){
//        return ResponseEntity.status(HttpStatus.OK).body(adminService.getLastTransactionsByCardId(id,limit));
//    }

    /* todo: последние транзакции по карте

    */

}
