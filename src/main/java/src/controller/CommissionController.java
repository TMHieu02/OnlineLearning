package src.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import src.config.annotation.ApiPrefixController;
import src.model.Commission;
import src.service.Commission.Dto.CommissionCreateDto;
import src.service.Commission.Dto.CommissionDto;
import src.service.Commission.Dto.CommissionUpdateDto;
import src.service.Commission.CommissionService;

import java.util.List;
import java.util.concurrent.CompletableFuture;
@RestController
@ApiPrefixController(value = "/commissions")
public class CommissionController {
    @Autowired
    private CommissionService commissionService;
    @GetMapping( "/{id}")
    public CompletableFuture<CommissionDto> findOneById(@PathVariable int id) {
        return commissionService.getOne(id);
    }
    @GetMapping()
    public CompletableFuture<List<CommissionDto>> findAll() {
        return commissionService.getAll();
    }
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public CompletableFuture<Commission> create(@RequestBody CommissionCreateDto input) {
        return commissionService.create(input);
    }
    @PatchMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CompletableFuture<CommissionDto> update(@PathVariable int id, CommissionUpdateDto commission) {
        return commissionService.update(id, commission);
    }
    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CompletableFuture<String> deleteById(@PathVariable int id) {
        return commissionService.deleteById(id);
    }
}
