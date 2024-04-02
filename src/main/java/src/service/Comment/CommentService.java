package src.service.Comment;

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
import src.model.Comment;
import src.model.User;
import src.repository.CommentRepository;
import src.service.Comment.Dto.CommentCreateDto;
import src.service.Comment.Dto.CommentDto;
import src.service.Comment.Dto.CommentUpdateDto;
import src.service.Comment.Dto.CommentUserDTO;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static java.sql.Types.NULL;
@Service
public class CommentService {
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private ModelMapper toDto;
    @PersistenceContext
    EntityManager em;
    @Async
    public CompletableFuture<List<CommentDto>> getAll() {
        return CompletableFuture.completedFuture(
                commentRepository.findAll().stream().map(
                        x -> toDto.map(x, CommentDto.class)
                ).collect(Collectors.toList()));
    }
    @Async
    public CompletableFuture<CommentDto> getOne(int id) {
        Comment comment = commentRepository.findById(id).orElse(null);
        if (comment == null) {
            throw new NotFoundException("Không tìm thấy quyền với ID " + id);
        }
        return CompletableFuture.completedFuture(toDto.map(comment, CommentDto.class));
    }
    public CompletableFuture<Comment> createComment(CommentCreateDto commentCreateDto) {
        CompletableFuture<Comment> future = new CompletableFuture<>();
        Comment newComment = new Comment();
        newComment.setContent(commentCreateDto.getContent());
        newComment.setUserId(commentCreateDto.getUserId());
        newComment.setVideoId(commentCreateDto.getVideoId());
        newComment.setParentCommentId(commentCreateDto.getParentCommentId());
        Comment savedComment = commentRepository.save(newComment);
        future.complete(savedComment);
        return future;
    }
    public Comment updateComment(int commentId, Map<String, Object> fieldsToUpdate) {
        Optional<Comment> optionalComment = commentRepository.findById(commentId);
        if (optionalComment.isPresent()) {
            Comment comment = optionalComment.get();
            updateCommentFields(comment, fieldsToUpdate);
            comment.setUpdateAt(new Date());
            commentRepository.save(comment);
            return comment;
        }
        return null;
    }
    private void updateCommentFields(Comment comment, Map<String, Object> fieldsToUpdate) {
        for (Map.Entry<String, Object> entry : fieldsToUpdate.entrySet()) {
            String fieldName = entry.getKey();
            Object value = entry.getValue();
            updateCommentField(comment, fieldName, value);
        }
    }
    private void updateCommentField(Comment comment, String fieldName, Object value) {
        switch (fieldName) {
            case "content":
                comment.setContent((String) value);
                break;
            case "videoId":
                comment.setVideoId((int) value);
                break;
            case "userId":
                comment.setUserId((int) value);
                break;
            case "parentCommentId":
                comment.setParentCommentId((int) value);
                break;
            default:
                break;
        }
    }
    @Async
    public CompletableFuture<String> deleteById(int id) {
        Optional<Comment> commentOptional = commentRepository.findById(id);
        if (!commentOptional.isPresent()) {
            return CompletableFuture.completedFuture("Không có ID này");
        }
        try {
            Comment comment = commentOptional.get();
            comment.setIsDeleted(true);
            comment.setUpdateAt(new Date(new java.util.Date().getTime()));
            commentRepository.save(comment);
            return CompletableFuture.completedFuture("Đánh dấu xóa thành công");
        } catch (Exception e) {
            return CompletableFuture.completedFuture("Xóa không được");
        }
    }
    @Async
    public CompletableFuture<List<CommentDto>> getCommentFeatures(int parentCommentId) {
        if (parentCommentId == NULL) {
            throw new IllegalArgumentException("Parent Comment ID cannot be null");
        }
        List<Comment> categories = commentRepository.findAllByParentCommentId(parentCommentId);
        if (categories == null) {
            throw new NotFoundException("Unable to find categories with parent comment ID " + parentCommentId);
        }
        return CompletableFuture.completedFuture(categories.stream().map(
                x -> toDto.map(x, CommentDto.class)
        ).collect(Collectors.toList()));
    }
    public List<CommentUserDTO> getVideosByUserId(int videoId) {
        List<CommentUserDTO> result = new ArrayList<>();
        List<Comment> comments = commentRepository.findByVideoId(videoId);
        for (Comment comment : comments) {
            CommentUserDTO dto = new CommentUserDTO();
            dto.setCommentId(comment.getId());
            dto.setContent(comment.getContent());
            dto.setFullname(comment.getUserByUserId().getFullname());
            dto.setAvatar(comment.getUserByUserId().getAvatar());
            dto.setUserId(comment.getUserByUserId().getId());
            dto.setVideoId(comment.getVideoId());
            dto.setIsDeleted(comment.getIsDeleted());
            dto.setCreate(comment.getCreateAt());
            dto.setUpdate(comment.getUpdateAt());
            result.add(dto);
        }
        return result;
    }
}
