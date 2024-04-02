package src.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import src.model.Comment;

import java.util.List;

@Repository
public interface CommentRepository  extends JpaRepository<Comment, Integer> {
    List<Comment> findAllByParentCommentId(int parentCommentId);

    @Query("SELECT c FROM Comment c WHERE c.videoId = :videoId AND (c.isDeleted = false OR c.isDeleted IS NULL)")
    List<Comment> findByVideoId(@Param("videoId") int videoId);

}
