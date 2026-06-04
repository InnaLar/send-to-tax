package larina.lessons.send_to_tax.repository;

import larina.lessons.send_to_tax.model.entity.Receipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReceiptRepository extends JpaRepository<Receipt, Long> {
    @Query(value = """
                select * from receipts r
                where r.processed = false and COALESCE(r.status, '') <> 'FAILED' limit :limit
            """, nativeQuery = true)
    List<Receipt> findAllByProcessedFalse(int limit);

    @Query(value = """
                select * from receipts r
                where r.processed = true limit :limit
            """, nativeQuery = true)
    List<Receipt> findAllByProcessedTrue(int limit);

    @Query(value = """
                select * from receipts r
                where r.status = :status limit :limit
            """, nativeQuery = true)
    List<Receipt> findAllByStatus(String status, int limit);
}
