package src.service.Category;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.HttpServletRequest;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import src.config.dto.PagedResultDto;
import src.config.dto.Pagination;
import src.config.exception.NotFoundException;
import src.config.utils.ApiQuery;
import src.model.Category;

import src.repository.CategoryRepository;
import src.service.Category.Dto.CategoryCreateDto;
import src.service.Category.Dto.CategoryDto;



import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static java.sql.Types.NULL;
@Service
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ModelMapper toDto;
    @PersistenceContext
    EntityManager em;
    @Async
    public CompletableFuture<List<CategoryDto>> getAll() {
        return CompletableFuture.completedFuture(
                categoryRepository.findAll().stream().map(
                        x -> toDto.map(x, CategoryDto.class)
                ).collect(Collectors.toList()));
    }
    @Async
    public CompletableFuture<CategoryDto> getOne(int id) {
        Category category = categoryRepository.findById(id).orElse(null);
        if (category == null) {
            throw new NotFoundException("Không tìm thấy quyền với ID " + id);
        }
        return CompletableFuture.completedFuture(toDto.map(category, CategoryDto.class));
    }
    public CompletableFuture<Category> createCategory(CategoryCreateDto categoryCreateDto) {
        CompletableFuture<Category> future = new CompletableFuture<>();
        Category newCategory = new Category();
        newCategory.setName(categoryCreateDto.getName());
        newCategory.setImage(categoryCreateDto.getImage());
        newCategory.setParentCategoryId(categoryCreateDto.getParentCategoryId());
        Category savedCategory = categoryRepository.save(newCategory);
        future.complete(savedCategory);

        return future;
    }
    public Category updateCategory(int categoryId, Map<String, Object> fieldsToUpdate) {
        Optional<Category> optionalCategory = categoryRepository.findById(categoryId);
        if (optionalCategory.isPresent()) {
            Category category = optionalCategory.get();
            updateCategoryFields(category, fieldsToUpdate);
            category.setUpdateAt(new Date());
            categoryRepository.save(category);
            return category;
        }
        return null;
    }
    private void updateCategoryFields(Category category, Map<String, Object> fieldsToUpdate) {
        for (Map.Entry<String, Object> entry : fieldsToUpdate.entrySet()) {
            String fieldName = entry.getKey();
            Object value = entry.getValue();
            updateCategoryField(category, fieldName, value);
        }
    }
    private void updateCategoryField(Category category, String fieldName, Object value) {
        switch (fieldName) {
            case "name":
                category.setName((String) value);
                break;
            case "image":
                category.setImage((String) value);
                break;
            case "parentCategoryId":
                category.setParentCategoryId((int) value);
                break;
            default:
                break;
        }
    }
    @Async
    public CompletableFuture<String> deleteById(int id) {
        Optional<Category> categoryOptional = categoryRepository.findById(id);
        if (!categoryOptional.isPresent()) {
            return CompletableFuture.completedFuture("Không có ID này");
        }
        try {
            Category category = categoryOptional.get();
            category.setIsDeleted(true);
            category.setUpdateAt(new Date(new java.util.Date().getTime()));
            categoryRepository.save(category);
            return CompletableFuture.completedFuture("Đánh dấu xóa thành công");
        } catch (Exception e) {
            return CompletableFuture.completedFuture("Xóa không được");
        }
    }
    @Async
    public CompletableFuture<List<CategoryDto>> getCategoryFeatures(int parentCategoryId) {
        if (parentCategoryId == NULL) {
            throw new IllegalArgumentException("Parent category ID cannot be null");
        }
        List<Category> categories = categoryRepository.findAllByParentCategoryId(parentCategoryId);
        if (categories == null) {
            throw new NotFoundException("Unable to find categories with parent category ID: " + parentCategoryId);
        }
        return CompletableFuture.completedFuture(categories.stream().map(
                x -> toDto.map(x, CategoryDto.class)
        ).collect(Collectors.toList()));
    }
}
