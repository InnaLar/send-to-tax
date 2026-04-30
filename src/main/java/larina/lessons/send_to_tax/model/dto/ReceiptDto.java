package larina.lessons.send_to_tax.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReceiptDto {
    private Long id;
    private String sum;
}
