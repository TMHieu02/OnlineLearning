package src.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import src.config.annotation.ApiPrefixController;

import src.model.Orders;
import src.service.Orders.Dto.OrderCreateDto;
import src.service.Orders.Dto.OrderDto;
import src.service.Orders.Dto.OrderUpdateDto;
import src.service.Orders.OrderService;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@ApiPrefixController(value = "/orders")
public class OrderController {
    @Autowired
    private OrderService orderService;
    @GetMapping()
    public CompletableFuture<List<OrderDto>> findAll() {
        return orderService.getAll();
    }
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CompletableFuture<OrderDto> getOne(@PathVariable int id) {
        return orderService.getOne(id);
    }
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CompletableFuture<Orders>> createOrder(@RequestBody OrderCreateDto orderCreateDto) {
        CompletableFuture<Orders> future = orderService.createOrder(orderCreateDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(future);
    }
    @PatchMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CompletableFuture<OrderDto> update(@PathVariable int id, OrderUpdateDto order) {
        return orderService.update(id, order);
    }
    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CompletableFuture<String> deleteById(@PathVariable int id) {
        return orderService.deleteById(id);
    }
}
