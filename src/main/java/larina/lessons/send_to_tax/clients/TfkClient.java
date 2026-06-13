package larina.lessons.send_to_tax.clients;

import larina.lessons.send_to_tax.model.dto.ReceiptDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.RestClient;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@RequiredArgsConstructor
public class TfkClient {
    private final RestClient restClient;

    public void sendRefund(Long refundId, Long receiptId, String sum) {

        ReceiptDto receiptDto = ReceiptDto.builder()
                .id(receiptId)
                .sum(sum)
                .build();

        restClient.post()
                .uri("/api/v1/refunds")
                .contentType(APPLICATION_JSON)
                .body(receiptDto)
                .retrieve()
                .onStatus(HttpStatusCode::isError, ((request, response) -> {
                    throw new RuntimeException(
                            "Ошибка запроса, статус: " + response.getStatusCode()
                    );
                }))
                .body(String.class);
    }
}
