package larina.lessons.send_to_tax.repository;

import larina.lessons.send_to_tax.model.entity.Refund;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefundRepository extends JpaRepository<Refund, Long> {

    Optional<Refund> findByReceiptId(Long receiptId);
}
