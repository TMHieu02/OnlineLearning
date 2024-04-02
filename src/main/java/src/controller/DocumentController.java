package src.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import src.config.annotation.ApiPrefixController;
import src.model.Document;
import src.service.Document.Dto.DocumentCreateDto;
import src.service.Document.Dto.DocumentDto;
import src.service.Document.DocumentService;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
@RestController
@ApiPrefixController(value = "/documents")
public class DocumentController {
    @Autowired
    private DocumentService documentService;
    @GetMapping()
    public CompletableFuture<List<DocumentDto>> findAll() {
        return documentService.getAll();
    }
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CompletableFuture<DocumentDto> getOne(@PathVariable int id) {
        return documentService.getOne(id);
    }
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public CompletableFuture<DocumentDto> create(@RequestBody DocumentCreateDto input) {
        return documentService.create(input);
    }
    @PatchMapping("/{documentId}")
    public ResponseEntity<Document> updateDocumentField(
            @PathVariable int documentId,
            @RequestBody Map<String, Object> fieldsToUpdate) {

            Document updatedDocument = documentService.updateDocument(documentId, fieldsToUpdate);
            if (updatedDocument != null) {
                return ResponseEntity.ok(updatedDocument);
            } else {
                return ResponseEntity.notFound().build();
            }

    }
    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CompletableFuture<String> deleteById(@PathVariable int id) {
        return documentService.deleteById(id);
    }

    @GetMapping("/course={courseId}")
    public CompletableFuture<List<DocumentDto>> findByCourseId(@PathVariable int courseId) {
        return documentService.findByCourseId(courseId);
    }
    @GetMapping("/countByCourse/{courseId}")
    public CompletableFuture<Integer> countDocumentsByCourseId(@PathVariable int courseId) {
        return CompletableFuture.supplyAsync(() -> documentService.countDocumentsByCourseId(courseId));
    }
}
