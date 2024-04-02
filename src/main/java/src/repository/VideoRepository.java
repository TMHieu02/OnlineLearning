package src.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import src.model.Video;

import java.util.List;
import java.util.Optional;

@Repository
public interface VideoRepository extends JpaRepository<Video, Integer> {
    List<Video> findByCourseId(int courseId);

    @Query("SELECT COUNT(v) FROM Video v WHERE v.courseId = :courseId AND (v.isDeleted = false OR v.isDeleted IS NULL)")
    int countByCourseId(int courseId);
}
