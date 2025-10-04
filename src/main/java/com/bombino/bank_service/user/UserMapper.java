package com.bombino.bank_service.user;

import com.bombino.bank_service.model.mapper.Mappable;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper extends Mappable<User,UserDto> {
}
