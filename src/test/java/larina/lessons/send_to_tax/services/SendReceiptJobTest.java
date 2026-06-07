package larina.lessons.send_to_tax.services;

import larina.lessons.send_to_tax.model.entity.Receipt;
import larina.lessons.send_to_tax.model.entity.ReceiptDeliveredStatus;
import larina.lessons.send_to_tax.model.entity.Shedlock;
import larina.lessons.send_to_tax.model.entity.ShedlockStatus;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;

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
        doAnswer(invocation -> {
            Thread.sleep(1000);
            return null;
        }).when(taxClient).sendReceipt(any(), any());

        //WHEN
        List<Throwable> errors = Collections.synchronizedList(new ArrayList<>());

        Runnable task = () -> {
            try {
                sendReceiptJob.processReceipt();
            } catch (Throwable t) {
                errors.add(t);
            }
        };
        Thread t1 = new Thread(task);
        Thread t2 = new Thread(task);
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        //THEN
        Assertions.assertThat(errors).singleElement().isInstanceOfSatisfying(RuntimeException.class,
                e -> Assertions.assertThat(e.getMessage()).isEqualTo("Shedlock was locked by another process"));
    }

}
