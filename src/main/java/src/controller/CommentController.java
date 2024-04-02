package src.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import src.config.annotation.ApiPrefixController;
import src.config.dto.PagedResultDto;
import src.model.Comment;
import src.model.Comment;
import src.service.Comment.CommentService;
import src.service.Comment.Dto.CommentCreateDto;
import src.service.Comment.Dto.CommentDto;
import src.service.Comment.Dto.CommentUpdateDto;
import src.service.Comment.Dto.CommentUserDTO;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@ApiPrefixController(value = "/comments")
public class CommentController {
    @Autowired
    private CommentService commentService;
    @GetMapping()
    public CompletableFuture<List<CommentDto>> findAll() {
        return commentService.getAll();
    }
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CompletableFuture<CommentDto> getOne(@PathVariable int id) {
        return commentService.getOne(id);
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CompletableFuture<Comment>> createComment(@RequestBody CommentCreateDto CommentCreateDto) {
        CompletableFuture<Comment> future = commentService.createComment(CommentCreateDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(future);
    }
    @PatchMapping("/{commentId}")
    public ResponseEntity<Comment> updateCommentField(
            @PathVariable int commentId,
            @RequestBody Map<String, Object> fieldsToUpdate) {

            Comment updatedComment = commentService.updateComment(commentId, fieldsToUpdate);
            if (updatedComment != null) {
                return ResponseEntity.ok(updatedComment);
            } else {
                return ResponseEntity.notFound().build();
            }

    }
    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CompletableFuture<String> deleteById(@PathVariable int id) {
        return commentService.deleteById(id);
    }
    @GetMapping("/{id}/features")
    public CompletableFuture<List<CommentDto>> getCommentFeatures(@PathVariable("id") int parentCommentId) {
        return commentService.getCommentFeatures(parentCommentId);
    }
    @GetMapping("/video={videoId}")
    public CompletableFuture<ResponseEntity<List<CommentUserDTO>>> getVideosByUserId(@PathVariable int videoId) {
        return CompletableFuture.supplyAsync(() -> {
            List<CommentUserDTO> comments = commentService.getVideosByUserId(videoId);
            return new ResponseEntity<>(comments, HttpStatus.OK);
        });
    }
}
