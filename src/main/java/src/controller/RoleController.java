
package src.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import src.config.annotation.ApiPrefixController;

import src.service.Role.Dto.RoleDto;
import src.service.Role.Dto.RoleCreateDto;
import src.service.Role.Dto.RoleUpdateDto;
import src.service.Role.RoleService;

import java.util.List;
import java.util.concurrent.CompletableFuture;
@RestController
@ApiPrefixController(value = "/roles")
public class RoleController {
    @Autowired
    private RoleService roleService;
    @GetMapping()
    public CompletableFuture<List<RoleDto>> findAll() {
        return roleService.getAll();
    }
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CompletableFuture<RoleDto> getOne(@PathVariable int id) {
        return roleService.getOne(id);
    }
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public CompletableFuture<RoleDto> create(@RequestBody RoleCreateDto input) {
        return roleService.create(input);
    }
    @PatchMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CompletableFuture<RoleDto> update(@PathVariable int id, RoleUpdateDto role) {
        return roleService.update(id, role);
    }
    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CompletableFuture<String> deleteById(@PathVariable int id) {
        return roleService.deleteById(id);
    }

}
