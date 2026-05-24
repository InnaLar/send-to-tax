package larina.lessons.send_to_tax.services;

import larina.lessons.send_to_tax.clients.TaxClient;
import larina.lessons.send_to_tax.model.entity.Receipt;
import larina.lessons.send_to_tax.repository.ReceiptRepository;
import larina.lessons.send_to_tax.repository.ShedlockRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class SendReceiptJob {

    private final ReceiptRepository repository;
    private final ShedlockRepository shedlockRepository;
    private final TaxClient taxClient;
    private final LockService lockService;

    @Scheduled(cron = "*/10 * * * * *")
    public void processReceipt() {
        try {
            log.info("Start receipts' processing");
            if (lockService.lock("processReceipt")) {

                List<Receipt> receipts = repository.findAllByProcessedFalse(20);
                for (Receipt receipt : receipts) {
                    try {
                        taxClient.sendReceipt(receipt.getId(), receipt.getSum());
                        receipt.setProcessed(true);
                        repository.save(receipt);
                    } catch (Exception e) {
                        log.info("Request tax-service failed", e);
                    }
                }

                log.info("{} receipts processed", receipts.size());
            } else {
                log.info("method is running by other process");
            }
        } finally {
           lockService.unlock("processReceipt");
        }
    }

}
