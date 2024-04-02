package src.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import src.config.annotation.ApiPrefixController;
import src.config.dto.PagedResultDto;
import src.model.Course;
import src.service.Category.Dto.CategoryDto;
import src.service.Course.Dto.CourseCreateDto;
import src.service.Course.Dto.CourseDto;
import src.service.Course.Dto.CourseInfoDTO;
import src.service.Course.Dto.CourseUpdateDto;
import src.service.Course.CourseService;
import src.service.Video.Dto.VideoDto;

import java.awt.print.Pageable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
@RestController
@ApiPrefixController(value = "/courses")
public class CourseController {
    @Autowired
    private CourseService courseService;
    @GetMapping()
    public CompletableFuture<List<CourseDto>> findAll() {
        return courseService.getAll();
    }
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CompletableFuture<CourseDto> getOne(@PathVariable int id) {
        return courseService.getOne(id);
    }
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public CompletableFuture<CourseDto> create(@RequestBody CourseCreateDto input) {
        return courseService.create(input);
    }
    @PatchMapping("/{courseId}")
    public ResponseEntity<Course> updateCourseField(
            @PathVariable int courseId,
            @RequestBody Map<String, Object> fieldsToUpdate) {

            Course updatedCourse = courseService.updateCourse(courseId, fieldsToUpdate);
            if (updatedCourse != null) {
                return ResponseEntity.ok(updatedCourse);
            } else {
                return ResponseEntity.notFound().build();
            }

    }
    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CompletableFuture<String> deleteById(@PathVariable int id) {
        return courseService.deleteById(id);
    }
    @GetMapping("/calculateCourseRating")
    public CompletableFuture<Double> calculateCourseRating(@RequestParam int courseId) {
        return CompletableFuture.supplyAsync(() -> courseService.calculateCourseRating(courseId));
    }
    @GetMapping("/topNew")
    public CompletableFuture<List<CourseDto>> getTopNew() {
        return courseService.getTopNew();
    }
    @GetMapping("/topMost")
    public CompletableFuture<List<CourseDto>> getTopMost() {
        return courseService.getTopMost();
    }
    @GetMapping("/search/{title}")
    public CompletableFuture<List<CourseInfoDTO>> searchCoursesByTitle(
            @PathVariable String title,
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String sort) {

        return CompletableFuture.supplyAsync(() -> courseService.searchByTitle(title, pageNumber, pageSize, sort));
    }


    @GetMapping("/searchCategory/{categoryId}")
    public CompletableFuture<List<CourseInfoDTO>> getCoursesByCategoryId(@PathVariable int categoryId) {
        return CompletableFuture.supplyAsync(() -> courseService.getCoursesByCategoryId(categoryId));
    }
    @GetMapping("/user={userId}")
    public CompletableFuture<List<CourseInfoDTO>> findByUserId(@PathVariable int userId) {
        return CompletableFuture.supplyAsync(() -> courseService.findByUserId(userId));
    }

    @GetMapping(value = "/getCourseRelateInfo/{id}")
    public CompletableFuture<List<CourseInfoDTO>> getCourseRelateInfo(@PathVariable int id) {
        return CompletableFuture.supplyAsync(() -> courseService.getCourseAndRelateInfo(id));
    }
    @GetMapping(value = "/get4CourseNewRelateInfo")
    public CompletableFuture<List<CourseInfoDTO>> get4CourseNewRelateInfo() {
        return courseService.get4CourseNewAndRelateInfo();
    }
    @GetMapping(value = "/get4CourseRatingRelateInfo")
    public CompletableFuture<List<CourseInfoDTO>> get4CourseRatingRelateInfo() {
        return courseService.get4CourseRatingAndRelateInfo();
    }
    @GetMapping(value = "/get4CourseSoldRelateInfo")
    public CompletableFuture<List<CourseInfoDTO>> get4CourseSoldRelateInfo() {
        return courseService.get4CourseSoldAndRelateInfo();
    }
    @GetMapping("/findCouseAndRelateInfoByTitle/{title}")
    public CompletableFuture<List<CourseInfoDTO>> findCoursesAndRelateInfoByTitle(@PathVariable String title) {
        return CompletableFuture.supplyAsync(() -> courseService.findCourseSoldAndRelateInfoByTitle(title));
    }
    @GetMapping("/sortCourseInCategory/{categoryId}/sort_by={sortName}")
    public CompletableFuture<List<CourseInfoDTO>> sortCourseInCategory(
            @PathVariable int categoryId,
            @PathVariable String sortName,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) { // Sử dụng giá trị mặc định là 10 cho kích thước trang
        return CompletableFuture.supplyAsync(() -> courseService.sortCourseInCategory(categoryId, sortName, page, pageSize));
    }

    @GetMapping("/getCoursesAndRelateInfo")
    public CompletableFuture<List<CourseInfoDTO>> getCoursesAndRelateInfo() {
        return CompletableFuture.supplyAsync(() -> courseService.getCoursesAndRelateInfo());
    }
    @PatchMapping("/unlock-course/{id}")
    public CompletableFuture<ResponseEntity<String>> unlockCourse(@PathVariable int id) {
        return CompletableFuture.supplyAsync(() -> {
            String unlockResult = courseService.unLockCourse(id);
            return new ResponseEntity<>(unlockResult, HttpStatus.OK);
        });
    }
    @PatchMapping("/lock-course/{id}")
    public CompletableFuture<ResponseEntity<String>> lockCourse(@PathVariable int id) {
        return CompletableFuture.supplyAsync(() -> {
            String lockResult = courseService.lockCourse(id);
            return new ResponseEntity<>(lockResult, HttpStatus.OK);
        });
    }
    @GetMapping("/countByUsers/{userId}")
    public CompletableFuture<Integer> countVideosByCourseId(@PathVariable int userId) {
        return CompletableFuture.supplyAsync(() -> courseService.countCoursesByUserId(userId));
    }
    @PostMapping("/updateRating/{courseId}")
    public CompletableFuture<ResponseEntity<String>> updateCourseRating(@PathVariable int courseId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                courseService.updateCourseRatingByCourseId(courseId);
                return new ResponseEntity<>("Course rating updated successfully.", HttpStatus.OK);
            } catch (Exception e) {
                return new ResponseEntity<>("Failed to update course rating.", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        });
    }
    @GetMapping("/check/{courseId}/{userId}")
    public CompletableFuture<Boolean> checkVideoInCourse(@PathVariable int courseId, @PathVariable int userId) {
        return CompletableFuture.supplyAsync(() -> courseService.isCourseInUser(courseId, userId));
    }
    @GetMapping("/pagination/allcourse")
    public CompletableFuture<Page<CourseInfoDTO>> getPaginationAllCourse(
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        return CompletableFuture.supplyAsync(() -> courseService.findAllCourses(pageNo - 1, pageSize));
    }
    @GetMapping("/category-pagination/{categoryId}")
    public CompletableFuture<Page<CourseInfoDTO>> getCoursesByCategory(
            @PathVariable int categoryId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return CompletableFuture.supplyAsync(() -> courseService.getCoursesByCategoryId(categoryId, page - 1, size));
    }


}
