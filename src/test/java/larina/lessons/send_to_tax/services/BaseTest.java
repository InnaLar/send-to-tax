package larina.lessons.send_to_tax.services;

import larina.lessons.send_to_tax.clients.TaxClient;
import larina.lessons.send_to_tax.clients.TfkClient;
import larina.lessons.send_to_tax.model.entity.Shedlock;
import larina.lessons.send_to_tax.model.entity.ShedlockStatus;
import larina.lessons.send_to_tax.repository.ReceiptRepository;
import larina.lessons.send_to_tax.repository.RefundRepository;
import larina.lessons.send_to_tax.repository.ShedlockRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
public abstract class BaseTest {
    @Autowired
    protected ShedlockRepository shedlockRepository;
    @Autowired
    protected LockService lockService;
    @MockitoSpyBean
    protected ReceiptRepository receiptRepository;
    @Autowired
    protected SendReceiptJob sendReceiptJob;
    @MockitoBean
    protected TaxClient taxClient;
    @Autowired
    protected RefundService refundService;
    @MockitoSpyBean
    protected RefundRepository refundRepository;
    @MockitoBean
    protected TfkClient tfkClient;

    @DynamicPropertySource
    static void dataSource(DynamicPropertyRegistry registry) {
        // Берем данные из ОДНОГО запущенного контейнера
        registry.add("spring.datasource.url", PostgresInitializer.POSTGRES_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", PostgresInitializer.POSTGRES_CONTAINER::getUsername);
        registry.add("spring.datasource.password", PostgresInitializer.POSTGRES_CONTAINER::getPassword);
    }

    @BeforeEach
    void resetLock() {
        Shedlock shedlock = shedlockRepository.findByName("processReceipt").orElseThrow();
        shedlock.setStatus(ShedlockStatus.READY_TO_WORK);
        shedlockRepository.save(shedlock);
        refundRepository.deleteAll();
        receiptRepository.deleteAll();
    }
}
