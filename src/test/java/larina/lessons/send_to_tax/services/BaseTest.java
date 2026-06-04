package larina.lessons.send_to_tax.services;

import larina.lessons.send_to_tax.clients.TaxClient;
import larina.lessons.send_to_tax.model.entity.Shedlock;
import larina.lessons.send_to_tax.model.entity.ShedlockStatus;
import larina.lessons.send_to_tax.repository.ReceiptRepository;
import larina.lessons.send_to_tax.repository.ShedlockRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
public abstract class BaseTest {
    @Autowired
    protected ShedlockRepository shedlockRepository;
    @Autowired
    protected LockService lockService;
    @Autowired
    protected ReceiptRepository receiptRepository;
    @Autowired
    protected SendReceiptJob sendReceiptJob;
    @Autowired
    protected ReceiptPopulationJob receiptPopulationJob;
    @MockitoBean
    protected TaxClient taxClient;
    @Container
    private static final PostgreSQLContainer<?> POSTGRESQL_CONTAINER = new PostgreSQLContainer<>("postgres:14.5");

    @DynamicPropertySource
    static void dataSource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRESQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRESQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRESQL_CONTAINER::getPassword);
    }

    @BeforeEach
    void resetLock() {
        Shedlock shedlock = shedlockRepository.findByName("processReceipt").orElseThrow();
        shedlock.setStatus(ShedlockStatus.READY_TO_WORK);
        shedlockRepository.save(shedlock);
    }
}
