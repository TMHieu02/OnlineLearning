package src.service.Comment;

import src.service.Comment.Dto.CommentCreateDto;
import src.service.Comment.Dto.CommentDto;
import src.service.Comment.Dto.CommentUpdateDto;
import src.service.IService;

public interface ICommentService extends IService<CommentDto, CommentCreateDto, CommentUpdateDto> {
}
