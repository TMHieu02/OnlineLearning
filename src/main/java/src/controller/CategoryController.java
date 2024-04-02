
package src.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import src.config.annotation.ApiPrefixController;
import src.config.dto.PagedResultDto;

import src.model.Category;
import src.model.Category;
import src.service.Category.CategoryService;
import src.service.Category.Dto.CategoryCreateDto;
import src.service.Category.Dto.CategoryDto;
import src.service.Category.Dto.CategoryUpdateDto;


import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
@RestController
@ApiPrefixController(value = "/categories")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;
    @GetMapping()
    public CompletableFuture<List<CategoryDto>> findAll() {
        return categoryService.getAll();
    }
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CompletableFuture<CategoryDto> getOne(@PathVariable int id) {
        return categoryService.getOne(id);
    }
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CompletableFuture<Category>> createCategory(@RequestBody CategoryCreateDto categoryCreateDto) {
        CompletableFuture<Category> future = categoryService.createCategory(categoryCreateDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(future);
    }
    @PatchMapping("/{categoryId}")
    public ResponseEntity<Category> updateCategoryField(
            @PathVariable int categoryId,
            @RequestBody Map<String, Object> fieldsToUpdate) {

            Category updatedCategory = categoryService.updateCategory(categoryId, fieldsToUpdate);
            if (updatedCategory != null) {
                return ResponseEntity.ok(updatedCategory);
            } else {
                return ResponseEntity.notFound().build();
            }

    }
    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CompletableFuture<String> deleteById(@PathVariable int id) {
        return categoryService.deleteById(id);
    }
    @GetMapping("/{id}/features")
    public CompletableFuture<List<CategoryDto>> getCategoryFeatures(@PathVariable("id") int parentCategoryId) {
        return categoryService.getCategoryFeatures(parentCategoryId);
    }
}
