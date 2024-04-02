package src.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import src.model.Commission;
@Repository
public interface CommissionRepository  extends JpaRepository<Commission, Integer> {
}
