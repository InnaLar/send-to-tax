package larina.lessons.send_to_tax.services;


import larina.lessons.send_to_tax.exception.ErrorCode;
import larina.lessons.send_to_tax.exception.ServiceException;
import larina.lessons.send_to_tax.model.entity.Receipt;
import larina.lessons.send_to_tax.model.entity.ReceiptStatus;
import larina.lessons.send_to_tax.model.entity.Refund;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;

public class PostRefundTest extends BaseTest {
    @Test
    void testPostRefundSuccess() {
        // given
        Receipt receipt = Receipt.builder()
                .sum("100")
                .status(ReceiptStatus.NEW)
                .build();
        Receipt receiptSaved = receiptRepository.save(receipt);
        Long receiptId = receiptSaved.getId();
        // when
        Mockito.when(receiptRepository.findById(receiptId)).thenReturn(java.util.Optional.of(receipt));
        Mockito.doNothing().when(tfkClient).sendRefund(anyLong(), anyLong(), anyString());
        refundService.postRefund(receiptId);
        // then
        Refund refundByReceiptId = refundRepository.findByReceiptId(receiptId).orElseThrow();
        /*Assertions.assertThat(refundByReceiptId)
                .returns(RefundStatus.NEW, Refund::getStatus);*/
        Assertions.assertThat(refundByReceiptId.getReceipt().getId()).isEqualTo(receiptId);
        Assertions.assertThat(refundByReceiptId.getReceipt().getStatus()).isEqualTo(ReceiptStatus.REFUNDED);
        Mockito.verify(tfkClient, Mockito.times(1)).sendRefund(anyLong(), anyLong(), anyString());
    }

    @Test
    void testPostRefundFailedWhenCallTfkClientFailed() {
        // given
        Receipt receipt = Receipt.builder()
                .sum("100")
                .status(ReceiptStatus.NEW)
                .build();
        Receipt receiptSaved = receiptRepository.save(receipt);
        Long receiptId = receiptSaved.getId();
        Mockito.doThrow(new ServiceException(ErrorCode.ERR_CODE_005, receiptId)).when(tfkClient).sendRefund(anyLong(), anyLong(), anyString());
        // when
        Assertions.assertThatThrownBy(() -> refundService.postRefund(receiptId))
                .isInstanceOf(ServiceException.class);
        // then
        Assertions.assertThat(refundRepository.findByReceiptId(receiptId)).isEmpty();
        Receipt receiptTested = receiptRepository.findById(receiptId).orElseThrow();
        Assertions.assertThat(receiptTested).extracting(Receipt::getStatus).isNotEqualTo(ReceiptStatus.REFUNDED);
        Mockito.verify(tfkClient, Mockito.times(1)).sendRefund(anyLong(), anyLong(), anyString());
    }


}
