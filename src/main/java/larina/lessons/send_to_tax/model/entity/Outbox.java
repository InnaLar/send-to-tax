package larina.lessons.send_to_tax.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
@Setter
@RequiredArgsConstructor
@SuperBuilder
@Entity
public class Outbox extends BaseEntity{
    @Column(name = "receipt_id")
    private Long receiptId;
    @Column(name = "sent_at")
    private LocalDateTime sentAt;
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private SendStatus status;
}
