package larina.lessons.send_to_tax.services;

import larina.lessons.send_to_tax.model.entity.Shedlock;
import larina.lessons.send_to_tax.model.entity.ShedlockStatus;
import larina.lessons.send_to_tax.repository.ShedlockRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Воспроизводит race condition в LockService.lock():
 * <p>
 * Оба потока вызывают findByName до того, как кто-либо сделал save.
 * Оба видят Optional.empty() → оба вставляют запись → оба возвращают false
 * (т.е. оба считают, что захватили лок и продолжают работу).
 * <p>
 * FOR UPDATE в findByName помогает только когда запись УЖЕ существует.
 * При первом старте, когда записи нет, блокировать нечего — отсюда и гонка.
 */
@Slf4j
@SpringBootTest
@Testcontainers
@Disabled
class LockServiceRaceConditionTest {
    @Autowired
    private ShedlockRepository shedlockRepository;
    @Autowired
    private LockService lockService;
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

    @Test
    void lockShouldFalseWhenLocked() throws InterruptedException {
        var locked1 = lockService.lock("processReceipt", "processId1");
        var locked2 = lockService.lock("processReceipt", "processId2");
        assertThat(locked2).isFalse();
    }

    @Test
    void secondThreadNotWorkWhenTwoThreadStartsSimultaneously() throws InterruptedException {

        List<Boolean> results = Collections.synchronizedList(new ArrayList<>());

        Thread t1 = new Thread(() -> results.add(lockService.lock("processReceipt", "processId1")));
        Thread t2 = new Thread(() -> results.add(lockService.lock("processReceipt", "processId2")));

        t1.start();
        t2.start();
        t1.join();
        t2.join();

        assertThat(results).containsExactly(true, false);
    }

    @Test
    void lockShouldTrueWhenReadyToWork() throws InterruptedException {
        var locked1 = lockService.lock("processReceipt", "processId1");
        assertThat(locked1).isTrue();
        ShedlockStatus status = shedlockRepository.findByName("processReceipt").orElseThrow().getStatus();
        assertThat(status).isEqualTo(ShedlockStatus.IN_PROCESS);
    }
}
