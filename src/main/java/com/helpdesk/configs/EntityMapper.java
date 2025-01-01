package com.helpdesk.configs;

import java.util.List;

public interface EntityMapper<D, E> {
    public E toEntity(D dto);
    public D toDto(E entity);
    public List<E> toEntity(List<D> dtoList);
    public List<D> toDto(List<E> entityList);
}
