package src.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import src.config.annotation.ApiPrefixController;
import src.config.dto.PagedResultDto;
import src.service.OrderItem.Dto.OrderItemCreateDto;
import src.service.OrderItem.Dto.OrderItemDto;
import src.service.OrderItem.Dto.OrderItemUpdateDto;
import src.service.OrderItem.OrderItemService;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@ApiPrefixController(value = "/orderItems")
public class OrderItemController {
    @Autowired
    private OrderItemService orderItemService;
    @GetMapping()
    public CompletableFuture<List<OrderItemDto>> findAll() {
        return orderItemService.getAll();
    }
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CompletableFuture<OrderItemDto> getOne(@PathVariable int id) {
        return orderItemService.getOne(id);
    }
    @GetMapping("/pagination")
    public CompletableFuture<PagedResultDto<OrderItemDto>> findAllPagination(HttpServletRequest request, @RequestParam(required = false, defaultValue = "0") Integer page ,
                                                                        @RequestParam(required = false, defaultValue = "10") Integer size,
                                                                        @RequestParam(required = false, defaultValue = "createAt") String orderBy) {
        return orderItemService.findAllPagination(request, size, page * size);
    }
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public CompletableFuture<OrderItemDto> create(@RequestBody OrderItemCreateDto input) {
        return orderItemService.create(input);
    }
    @PatchMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CompletableFuture<OrderItemDto> update(@PathVariable int id, OrderItemUpdateDto orderItem) {
        return orderItemService.update(id, orderItem);
    }
    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CompletableFuture<String> deleteById(@PathVariable int id) {
        return orderItemService.deleteById(id);
    }
}
