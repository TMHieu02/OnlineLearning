package src.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import src.config.annotation.ApiPrefixController;
import src.config.dto.PagedResultDto;
import src.model.Video;
import src.service.Video.VideoService;
import src.service.Video.Dto.VideoCreateDto;
import src.service.Video.Dto.VideoDto;

import java.util.List;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
@RestController
@ApiPrefixController(value = "/videos")
public class VideoController {
    @Autowired
    private VideoService videoService;
    @GetMapping()
    public CompletableFuture<List<VideoDto>> findAll() {
        return videoService.getAll();
    }
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CompletableFuture<VideoDto> getOne(@PathVariable int id) {
        return videoService.getOne(id);
    }
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CompletableFuture<Video>> createVideo(@RequestBody VideoCreateDto videoCreateDto) {
        CompletableFuture<Video> future = videoService.createVideo(videoCreateDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(future);
    }
    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CompletableFuture<String> deleteById(@PathVariable int id) {
        return videoService.deleteById(id);
    }
    @PatchMapping("/{videoId}")
    public ResponseEntity<Video> updateVideoField(
            @PathVariable int videoId,
            @RequestBody Map<String, Object> fieldsToUpdate) {

            Video updatedVideo = videoService.updateVideo(videoId, fieldsToUpdate);
            if (updatedVideo != null) {
                return ResponseEntity.ok(updatedVideo);
            } else {
                return ResponseEntity.notFound().build();
            }

    }
    @GetMapping("/course={courseId}")
    public CompletableFuture<List<VideoDto>> findByCourseId(@PathVariable int courseId) {
        return videoService.findByCourseId(courseId);
    }
    @GetMapping("/countByCourse/{courseId}")
    public CompletableFuture<Integer> countVideosByCourseId(@PathVariable int courseId) {
        return CompletableFuture.supplyAsync(() -> videoService.countVideosByCourseId(courseId));
    }
    @GetMapping("/check/{videoId}/{courseId}")
    public CompletableFuture<Boolean> checkVideoInCourse(@PathVariable int videoId, @PathVariable int courseId) {
        return CompletableFuture.supplyAsync(() -> videoService.isVideoInCourse(videoId, courseId));
    }

}


