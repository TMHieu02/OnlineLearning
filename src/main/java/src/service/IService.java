package src.service;


import jakarta.servlet.http.HttpServletRequest;
import src.config.dto.PagedResultDto;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface IService<T, D, E> {
    CompletableFuture<List<T>> getAll();

    CompletableFuture<T> getOne(int id);

    CompletableFuture<Void> deleteById(int id);

    CompletableFuture<T> create(D input);

    CompletableFuture<T> update(int id, E input);

    CompletableFuture<PagedResultDto<T>> findAllPagination(HttpServletRequest request, Integer limit, Integer skip);

}

