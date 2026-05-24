package larina.lessons.send_to_tax.repository;

import larina.lessons.send_to_tax.model.entity.Shedlock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ShedlockRepository extends JpaRepository<Shedlock, Long> {
    @Query(value = """
            select * from shedlock sh where sh.name = :name /*for update*/
            """, nativeQuery = true)
    Optional<Shedlock> findByName(String name);
}
