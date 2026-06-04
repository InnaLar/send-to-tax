package larina.lessons.send_to_tax.services;

import larina.lessons.send_to_tax.model.entity.Shedlock;
import larina.lessons.send_to_tax.model.entity.ShedlockStatus;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;


public class LockServiceTest extends BaseTest {
    @Test
    void testCancelLockWhenStartDateExpired() {
        //Given
        Shedlock shedlock
                = shedlockRepository.findByName("processReceipt").orElseThrow();
        shedlock.setStatus(ShedlockStatus.IN_PROCESS);
        shedlock.setStartTime(Instant.now().minus(35, ChronoUnit.MINUTES));
        shedlockRepository.save(shedlock);
        //When
        boolean result = lockService.lock("processReceipt");
        //Then
        Assertions.assertThat(result).isTrue();
    }

    @Test
    void testLockWhenStartDateNotExpired() {
        //Given
        Shedlock shedlock
                = shedlockRepository.findByName("processReceipt").orElseThrow();
        shedlock.setStatus(ShedlockStatus.IN_PROCESS);
        shedlock.setStartTime(Instant.now().minus(15, ChronoUnit.MINUTES));
        shedlockRepository.save(shedlock);
        //When
        boolean result = lockService.lock("processReceipt");
        //Then
        Assertions.assertThat(result).isFalse();
    }
}
