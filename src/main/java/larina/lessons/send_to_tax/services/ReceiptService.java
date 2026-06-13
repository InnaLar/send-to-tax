package larina.lessons.send_to_tax.services;

import larina.lessons.send_to_tax.model.entity.Receipt;
import larina.lessons.send_to_tax.repository.ReceiptRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Service
@RequiredArgsConstructor
public class ReceiptService {
    private final ReceiptRepository receiptRepository;
    public Optional<Receipt> findById(Long id) {
        return receiptRepository.findById(id);
    }
}
