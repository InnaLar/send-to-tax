package larina.lessons.send_to_tax.model.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@SuperBuilder
@Entity
@Table(name = "refunds")
public class Refund extends BaseEntity {
    @OneToOne
    @JoinColumn(name = "receipt_id", referencedColumnName = "id")
    private Receipt receipt;
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
