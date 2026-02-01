package com.henrique.catalog.domain.dto.global;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public record PaginationParams(Integer page,
                               Integer pageSize) {
    public PaginationParams {
        // Se 'page' for nulo, assume 0. Se for negativo, também (opcional).
        page = (page == null || page < 0) ? 0 : page;

        // Se 'pageSize' for nulo, assume 10.
        pageSize = (pageSize == null || pageSize < 1) ? 10 : pageSize;
    }

    public Pageable toPageable() {
//        PEGANDO DO QUE FOI CRIADO POR ULTIMO ATE O MAIS VELHO
        return PageRequest.of(page, pageSize, Sort.by("createdAt").descending());
    }
}
