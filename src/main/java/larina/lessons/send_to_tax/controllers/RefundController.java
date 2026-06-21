package larina.lessons.send_to_tax.controllers;

import larina.lessons.send_to_tax.exception.ServiceException;
import larina.lessons.send_to_tax.services.RefundService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
//@RequestMapping(" /api/v1/receipts/")
@RequiredArgsConstructor
public class RefundController {
    private final RefundService refundService;

    @PostMapping("/api/v1/receipts/{id}/refund")
    public ResponseEntity<?> sendRefund(@PathVariable Long id) {
        try {
            refundService.postRefund(id);
        } catch (ServiceException e) {
            return ResponseEntity
                    .status(e.getHttpCode())
                    .body(e.getMessage());
        }
        return ResponseEntity.ok().build();
    }
}
