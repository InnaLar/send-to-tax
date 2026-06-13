package larina.lessons.send_to_tax.repository;

import larina.lessons.send_to_tax.model.entity.Refund;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefundRepository extends JpaRepository<Refund, Long> {
}
