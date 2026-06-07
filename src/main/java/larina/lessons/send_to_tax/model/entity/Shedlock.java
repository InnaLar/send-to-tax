package larina.lessons.send_to_tax.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "shedlock")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Shedlock {
    @Id
    @Column
    private Long id;
    @Column
    private String name;
    @Column(name = "start_time")
    private Instant startTime;
    @Column
    @Enumerated(EnumType.STRING)
    private ShedlockStatus status;
    @Column(name = "process_id")
    private String processId;
}
