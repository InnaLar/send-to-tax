package larina.lessons.send_to_tax.model.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.time.LocalTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
/*@Builder*/
@SuperBuilder
@Entity
@Table(name = "refunds")
public class Refund extends BaseEntity {
    /*@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;*/
    @OneToOne
    @JoinColumn(name = "receipt_id", referencedColumnName = "id")
    private Receipt receipt;
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    /*@Column
    @Enumerated(EnumType.STRING)
    private RefundStatus status;*/
}
