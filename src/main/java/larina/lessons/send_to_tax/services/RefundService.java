package larina.lessons.send_to_tax.services;

import larina.lessons.send_to_tax.clients.TfkClient;
import larina.lessons.send_to_tax.model.entity.Receipt;
import larina.lessons.send_to_tax.model.entity.ReceiptDeliveredStatus;
import larina.lessons.send_to_tax.model.entity.Refund;
import larina.lessons.send_to_tax.repository.RefundRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefundService {
    private final RefundRepository refundRepository;
    private final ReceiptService receiptService;
    private final TfkClient tfkClient;

    public void postRefund(Long id) {
        Receipt receipt = receiptService.findById(id).get();
        Refund refund = Refund.builder()
                .receipt(receipt)
                .sum(receipt.getSum())
                .build();
        Refund refundSaved = refundRepository.save(refund);
        try {
            tfkClient.sendRefund(refundSaved.getId(), receipt.getId(), receipt.getSum());
            receipt.setStatus(ReceiptDeliveredStatus.REFUNDED);
        } catch (Exception e) {
            refundRepository.delete(refundSaved);
            throw new RuntimeException("Call TfkClient failed");
        }

    }
}
