package larina.lessons.send_to_tax.controllers;

import larina.lessons.send_to_tax.model.entity.ReceiptDeliveredStatus;
import larina.lessons.send_to_tax.services.ReceiptService;
import larina.lessons.send_to_tax.services.RefundService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(" /api/v1/receipts/")
@RequiredArgsConstructor
public class RefundController {
    private final RefundService refundService;
    private final ReceiptService receiptService;

    @PostMapping("{id}/refund")
    public ResponseEntity<?> sendRefund(@PathVariable Long id) {
        if (!checkIfReceiptExists(id)) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Article not found.");
        }
        if (receiptService.findById(id).get().getStatus().equals(ReceiptDeliveredStatus.REFUNDED)) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body("Article is already refunded.");
        }
        try {
            refundService.postRefund(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body("Call TfkClientFailed.");
        }

    }

    private boolean checkIfReceiptExists(Long id) {
        return receiptService.findById(id).isPresent();
    }
}
