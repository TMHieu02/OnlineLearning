package src.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import src.Dto.ReviewUserDTO;
import src.config.annotation.ApiPrefixController;
import src.model.Review;
import src.service.Review.Dto.ReviewDto;
import src.service.Review.Dto.ReviewCreateDto;
import src.service.Review.ReviewService;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
@RestController
@ApiPrefixController(value = "/reviews")
public class ReviewController {
    @Autowired
    private ReviewService reviewService;
    @GetMapping()
    public CompletableFuture<List<ReviewDto>> findAll() {
        return reviewService.getAll();
    }
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CompletableFuture<ReviewDto> getOne(@PathVariable int id) {
        return reviewService.getOne(id);
    }
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public CompletableFuture<ReviewDto> create(@RequestBody ReviewCreateDto input) {
        return reviewService.create(input);
    }
    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CompletableFuture<String> deleteById(@PathVariable int id) {
        return reviewService.deleteById(id);
    }
    @GetMapping("/user={userId}")
    public CompletableFuture<List<ReviewDto>> findByUserId(@PathVariable int userId) {
        return reviewService.findByUserId(userId);
    }
    @PatchMapping("/{userId}")
    public ResponseEntity<Review> updateReviewField(
            @PathVariable int userId,
            @RequestBody Map<String, Object> fieldsToUpdate) {

            Review updatedReview = reviewService.updateReview(userId, fieldsToUpdate);
            if (updatedReview != null) {
                return ResponseEntity.ok(updatedReview);
            } else {
                return ResponseEntity.notFound().build();
            }

    }
    @GetMapping("/course={courseId}")
    public CompletableFuture<ResponseEntity<List<ReviewUserDTO>>> getReviewsByUserId(@PathVariable int courseId) {
        return CompletableFuture.supplyAsync(() -> {
            List<ReviewUserDTO> reviews = reviewService.getReviewsByUserId(courseId);
            return new ResponseEntity<>(reviews, HttpStatus.OK);
        });
    }
    @GetMapping("/revivewer/{reviewId}")
    public CompletableFuture<ResponseEntity<ReviewUserDTO>> getReviewByReviewId(@PathVariable int reviewId) {
        return CompletableFuture.supplyAsync(() -> {
            ReviewUserDTO review = reviewService.getReviewByReviewId(reviewId);
            if (review != null) {
                return new ResponseEntity<>(review, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        });
    }
}
