package larina.lessons.send_to_tax.services;

import jakarta.transaction.Transactional;
import larina.lessons.send_to_tax.clients.TfkClient;
import larina.lessons.send_to_tax.exception.ErrorCode;
import larina.lessons.send_to_tax.exception.ServiceException;
import larina.lessons.send_to_tax.model.entity.*;
import larina.lessons.send_to_tax.repository.OutboxRepository;
import larina.lessons.send_to_tax.repository.ReceiptRepository;
import larina.lessons.send_to_tax.repository.RefundRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RefundService {
    private final RefundRepository refundRepository;
    private final ReceiptRepository receiptRepository;
    private final OutboxRepository outboxRepository;
    private final TfkClient tfkClient;

    public void postRefund(Long id) {
        Optional<Receipt> receiptById = receiptRepository.findById(id);
        if (receiptById.isEmpty()) {
            throw new ServiceException(ErrorCode.ERR_CODE_001, id);
        }
        Receipt receipt = receiptById.orElseThrow();
        if (receipt.getStatus().equals(ReceiptStatus.REFUNDED)) {
            throw new ServiceException(ErrorCode.ERR_CODE_003, receipt.getId());
        }
        saveRefundAndOutbox(receipt);
        sendCallAndProcessingOutbox(receipt);
    }

    @Transactional
    private void sendCallAndProcessingOutbox(Receipt receipt) {
        Optional<Refund> refundOptional = refundRepository.findByReceiptId(receipt.getId());
        if (refundOptional.isEmpty()) {
            throw new ServiceException(ErrorCode.ERR_CODE_002, receipt.getId());
        }
        Optional<Outbox> outboxOptional = outboxRepository.findByReceiptId(receipt.getId());
        if (outboxOptional.isEmpty()) {
            throw new ServiceException(ErrorCode.ERR_CODE_004, receipt.getId());
        }
        Outbox outbox = outboxOptional.orElseThrow();
        Refund refund = refundOptional.orElseThrow();
        try {
            tfkClient.sendRefund(refund.getId(), receipt.getId(), receipt.getSum());
        } catch (Exception e) {
            refundRepository.delete(refund);
            throw new ServiceException(ErrorCode.ERR_CODE_005, receipt.getId());
        }
        outbox.setStatus(SendStatus.SENT);
        outbox.setSentAt(LocalDateTime.now());
        receipt.setStatus(ReceiptStatus.REFUNDED);
        receiptRepository.save(receipt);
    }

    @Transactional
    private void saveRefundAndOutbox(Receipt receipt) {
        Refund refund = Refund.builder()
                .receipt(receipt)
                .createdAt(LocalDateTime.now())
                .build();
        refundRepository.save(refund);
        Outbox outbox = Outbox.builder()
                .receiptId(receipt.getId())
                .status(SendStatus.NEW)
                .build();
        outboxRepository.save(outbox);
    }
}
