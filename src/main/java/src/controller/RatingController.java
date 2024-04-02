package src.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import src.config.annotation.ApiPrefixController;
import src.config.dto.PagedResultDto;
import src.model.Rating;
import src.service.Rating.Dto.RatingCreateDto;
import src.service.Rating.Dto.RatingDto;
import src.Dto.RatingDTO;
import src.service.Rating.RatingService;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
@RestController
@ApiPrefixController(value = "/ratings")
public class RatingController {
    @Autowired
    private RatingService ratingService;
    @GetMapping()
    public CompletableFuture<List<RatingDto>> findAll() {
        return ratingService.getAll();
    }
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CompletableFuture<RatingDto> getOne(@PathVariable int id) {
        return ratingService.getOne(id);
    }
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public CompletableFuture<RatingDto> create(@RequestBody RatingCreateDto input) {
        return ratingService.create(input);
    }
    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CompletableFuture<String> deleteById(@PathVariable int id) {
        return ratingService.deleteById(id);
    }
    @PatchMapping("/{userId}")
    public ResponseEntity<Rating> updateRatingField(
            @PathVariable int userId,
            @RequestBody Map<String, Object> fieldsToUpdate) {

            Rating updatedRating = ratingService.updateRating(userId, fieldsToUpdate);
            if (updatedRating != null) {
                return ResponseEntity.ok(updatedRating);
            } else {
                return ResponseEntity.notFound().build();
            }

    }
    @GetMapping("/course/{courseId}/overall")
    public CompletableFuture<ResponseEntity<Double>> getOverallRating(@PathVariable int courseId) {
        return CompletableFuture.supplyAsync(() -> {
            Double overallRating = ratingService.calculateOverallRating(courseId);
            return ResponseEntity.ok(overallRating);
        });
    }
    @GetMapping("/course/{courseId}/distribution")
    public CompletableFuture<ResponseEntity<Map<Integer, Double>>> getRatingDistribution(@PathVariable int courseId) {
        return CompletableFuture.supplyAsync(() -> {
            Map<Integer, Double> ratingDistribution = ratingService.calculateRatingDistribution(courseId);
            return ResponseEntity.ok(ratingDistribution);
        });
    }
    @GetMapping("/course/{courseId}/students/count")
    public CompletableFuture<Long> countStudentsForCourse(@PathVariable int courseId) {
        return CompletableFuture.supplyAsync(() -> ratingService.countStudentsForCourse(courseId));
    }
    @GetMapping("/user={userId}/course={courseId}")
    public CompletableFuture<ResponseEntity<List<RatingDTO>>> getReviewsByUserId(@PathVariable int userId, @PathVariable int courseId) {
        return CompletableFuture.supplyAsync(() -> {
            List<RatingDTO> rating = ratingService.findRatingByUserCourse(userId, courseId);
            return new ResponseEntity<>(rating, HttpStatus.OK);
        });
    }

    @GetMapping("/{userId}/totalRatings")
    public CompletableFuture<Integer> getTotalRatingsByUserId(@PathVariable int userId) {
        return CompletableFuture.supplyAsync(() -> ratingService.getTotalRatingsByUserId(userId));
    }
}
