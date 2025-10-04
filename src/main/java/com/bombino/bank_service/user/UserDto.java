package com.bombino.bank_service.user;

import java.util.UUID;

public record UserDto (
        UUID id,
        String name,
        String password
){
}
