package src.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import src.model.Document;

import java.util.Optional;
import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Integer> {
    List<Document> findByCourseId(int courseId);

    @Query("SELECT COUNT(d) FROM Document d WHERE d.courseId = :courseId AND (d.isDeleted = false OR d.isDeleted IS NULL)")
    int countByCourseId(int courseId);
}
