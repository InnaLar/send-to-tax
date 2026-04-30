package larina.lessons.send_to_tax.repository;

import larina.lessons.send_to_tax.model.entity.Receipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReceiptRepository extends JpaRepository<Receipt, Long> {
    //@Lock(LockModeType.OPTIMISTIC_FORCE_INCREMENT)
    @Query(value = """
                select * from receipts r
                where r.processed = false limit :limit
            """, nativeQuery = true)
    List<Receipt> findAllByProcessedFalse(int limit);
}
