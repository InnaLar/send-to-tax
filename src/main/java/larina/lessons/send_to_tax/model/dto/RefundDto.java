package larina.lessons.send_to_tax.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RefundDto {
    private Long id;
    private Long receiptId;
    private String sum;
}
