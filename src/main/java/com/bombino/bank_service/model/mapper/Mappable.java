package com.bombino.bank_service.model.mapper;

import org.springframework.stereotype.Component;

import java.util.List;


public interface Mappable<E, D> {
    E toEntity(D dto);

    D toDto(E entity);

    List<D> toDto(List<E> eList);
}
