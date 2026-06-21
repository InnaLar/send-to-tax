package larina.lessons.send_to_tax.services;

import larina.lessons.send_to_tax.clients.TaxClient;
import larina.lessons.send_to_tax.model.entity.Receipt;
import larina.lessons.send_to_tax.model.entity.ReceiptStatus;
import larina.lessons.send_to_tax.repository.ReceiptRepository;
import larina.lessons.send_to_tax.repository.ShedlockRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class SendReceiptJob {

    private final ReceiptRepository repository;
    private final ShedlockRepository shedlockRepository;
    private final TaxClient taxClient;
    private final LockService lockService;
    private final static int LIMIT_TRY = 3;

   /* @Scheduled(cron = "${my.send.cron}")*/
    public void processReceipt() {
        String idProcess = Thread.currentThread().getName();
        try {
            log.info("Start receipts' processing");
            if (lockService.lock("processReceipt", idProcess)) {
                List<Receipt> receipts = repository.findAllByProcessedFalse(20);
                while (!receipts.isEmpty()) {

                    for (Receipt receipt : receipts) {
                        try {
                            taxClient.sendReceipt(receipt.getId(), receipt.getSum());
                            receipt.setProcessed(true);
                            repository.save(receipt);
                        } catch (Exception e) {
                            log.info("Request tax-service failed", e);
                            receipt.setAttempts(receipt.getAttempts() + 1);
                            if (receipt.getAttempts() >= LIMIT_TRY) {
                                receipt.setProcessed(true);
                                receipt.setStatus(ReceiptStatus.FAILED);
                            }
                            repository.save(receipt);
                        }
                    }
                    receipts = repository.findAllByProcessedFalse(20);
                    log.info("{} receipts processed", receipts.size());
                }
            } else {
                log.info("method is running by other process");
            }
        } finally {
            lockService.unlock("processReceipt", idProcess);
        }
    }

}
