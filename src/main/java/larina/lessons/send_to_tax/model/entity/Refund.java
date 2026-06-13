package larina.lessons.send_to_tax.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
@Entity
@Table(name = "refunds")
public class Refund {
    private Long id;
    private Receipt receipt;
    private String sum;
}
