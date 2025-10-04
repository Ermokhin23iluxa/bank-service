package com.bombino.bank_service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    @PostMapping("/reg")
    ResponseEntity<UserDto> registration(@RequestBody LoginDto loginDto){
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.registration(loginDto));
    }
    @PostMapping("/log")
    ResponseEntity<UserDto> login(@RequestBody LoginDto loginDto){
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.login(loginDto));
    }
    @GetMapping("/getId/{id}")
    ResponseEntity<UserDto> getUserById(@PathVariable("id") UUID id){
        return ResponseEntity.status(HttpStatus.OK).body(userService.findUserById(id));
    }
    @GetMapping()
    ResponseEntity <List<UserDto>> getAllUsers(){
        return ResponseEntity.status(HttpStatus.OK).body(userService.findAllUsers());
    }
}
