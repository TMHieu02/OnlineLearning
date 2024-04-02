package src.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import src.config.annotation.ApiPrefixController;
import src.model.Feedback;
import src.service.Feedback.Dto.FeedbackCreateDto;
import src.service.Feedback.Dto.FeedbackDto;
import src.service.Feedback.FeedbackService;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
@RestController
@ApiPrefixController(value = "/feedbacks")
public class FeedbackController {
    @Autowired
    private FeedbackService feedbackService;
    @GetMapping()
    public CompletableFuture<List<FeedbackDto>> findAll() {
        return feedbackService.getAll();
    }
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CompletableFuture<FeedbackDto> getOne(@PathVariable int id) {
        return feedbackService.getOne(id);
    }
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public CompletableFuture<FeedbackDto> create(@RequestBody FeedbackCreateDto input) {
        return feedbackService.create(input);
    }
    @PatchMapping("/{feedbackId}")
    public ResponseEntity<Feedback> updateFeedbackField(
            @PathVariable int feedbackId,
            @RequestBody Map<String, Object> fieldsToUpdate) {
        Feedback updatedFeedback = feedbackService.updateFeedback(feedbackId, fieldsToUpdate);
        if (updatedFeedback != null) {
            return ResponseEntity.ok(updatedFeedback);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CompletableFuture<String> deleteById(@PathVariable int id) {
        return feedbackService.deleteById(id);
    }
}
