package larina.lessons.send_to_tax.repository;

import larina.lessons.send_to_tax.model.entity.Outbox;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OutboxRepository extends JpaRepository<Outbox, Long> {
    Optional<Outbox> findByReceiptId(Long receiptId);
}
