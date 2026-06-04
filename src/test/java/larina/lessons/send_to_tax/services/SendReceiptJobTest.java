package larina.lessons.send_to_tax.services;

import larina.lessons.send_to_tax.model.entity.Receipt;
import larina.lessons.send_to_tax.model.entity.ReceiptDeliveredStatus;
import larina.lessons.send_to_tax.model.entity.Shedlock;
import larina.lessons.send_to_tax.model.entity.ShedlockStatus;
import larina.lessons.send_to_tax.repository.ReceiptRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class SendReceiptJobTest extends BaseTest {
    @Test
    public void testSendReceiptJobSuccess() {
        //GIVEN
        for (int i = 0; i < 60; i++) {
            Receipt receipt = Receipt.builder()
                    .processed(false)
                    .sum("100").
                    build();
            receiptRepository.save(receipt);
        }
        //WHEN
        Mockito.doNothing().when(taxClient).sendReceipt(any(), any());
        sendReceiptJob.processReceipt();
        //THEN
        List<Receipt> receipts = receiptRepository.findAll();
        Assertions.assertThat(receipts).hasSize(60);
        Assertions.assertThat(receipts).allMatch(receipt -> receipt.isProcessed());
        Mockito.verify(taxClient, Mockito.times(60)).sendReceipt(any(), any());
    }

    @Test
    public void testSendReceiptJobOneUnprocessedReceiptByException() {
        //GIVEN
        for (int i = 0; i < 2; i++) {
            Receipt receipt = Receipt.builder()
                    .processed(false)
                    .sum("100")
                    .build();
            receiptRepository.save(receipt);
        }
        Receipt receiptForException = Receipt.builder()
                .processed(false)
                .sum("200")
                .build();
        receiptRepository.save(receiptForException);
        //WHEN
        Mockito.doThrow(new RuntimeException()).when(taxClient).sendReceipt(any(), Mockito.eq("200"));
        Mockito.doNothing().when(taxClient).sendReceipt(any(), Mockito.eq("100"));
        sendReceiptJob.processReceipt();
        //THEN
        Assertions.assertThat(receiptRepository.findAllByProcessedTrue(5)).hasSize(3);
        Assertions.assertThat(receiptRepository.findAllByStatus(ReceiptDeliveredStatus.FAILED.toString(), 5)).hasSize(1);
        Assertions.assertThat(receiptRepository.findAllByStatus(ReceiptDeliveredStatus.FAILED.toString(), 5))
                .hasSize(1).first().extracting(Receipt::getAttempts).isEqualTo(3);
        Mockito.verify(taxClient, Mockito.times(5)).sendReceipt(any(), any());
    }

    @Test
    public void testSecondProcessUnlockWorkingProcess() throws InterruptedException {
        //GIVEN
        Receipt receipt = Receipt.builder()
                .processed(false)
                .sum("100")
                .build();
        receiptRepository.save(receipt);
        ReceiptRepository receiptRepository = Mockito.mock(ReceiptRepository.class);
        when(receiptRepository.findAllByProcessedFalse(20)).thenAnswer(invocation -> {
            Thread.sleep(2000);
            return Optional.of(receipt);
        });
        //WHEN
        Thread t1 = new Thread(() -> sendReceiptJob.processReceipt());
        Thread t2 = new Thread(() -> {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            sendReceiptJob.processReceipt();
        });

        t1.start();
        t2.start();
        t2.join();
        //THEN
        Assertions.assertThat(shedlockRepository.findByName("processReceipt")).isPresent().hasValueSatisfying(shedlock -> {
            Assertions.assertThat(shedlock).extracting(Shedlock::getStatus).isEqualTo(ShedlockStatus.READY_TO_WORK);
        });
        t1.join();
    }

}
