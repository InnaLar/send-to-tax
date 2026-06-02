package larina.lessons.send_to_tax.services;

import larina.lessons.send_to_tax.model.entity.Receipt;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;

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
        Assertions.assertEquals(60, receipts.size());
        receipts.forEach(receipt -> Assertions.assertTrue(receipt.isProcessed()));
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
        Mockito.doNothing().when(receiptPopulationJob).processJob();
        sendReceiptJob.processReceipt();
        //THEN
        List<Receipt> receipts = receiptRepository.findAll();
        Assertions.assertEquals(3, receipts.size());
        Assertions.assertEquals(3, receipts.stream().filter(Receipt::isProcessed).count());
        Assertions.assertEquals(0, receipts.stream().filter(receipt -> !receipt.isProcessed()).count());
        Mockito.verify(taxClient, Mockito.times(6)).sendReceipt(any(), any());
    }
}
