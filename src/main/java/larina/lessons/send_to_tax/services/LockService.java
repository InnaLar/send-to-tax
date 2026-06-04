package larina.lessons.send_to_tax.services;

import jakarta.transaction.Transactional;
import larina.lessons.send_to_tax.model.entity.Shedlock;
import larina.lessons.send_to_tax.model.entity.ShedlockStatus;
import larina.lessons.send_to_tax.repository.ShedlockRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class LockService {
    private final ShedlockRepository shedlockRepository;
    private static long lockTimeout;

    // 3. Spring автоматически внедрит сюда значение ПОСЛЕ вызова конструктора
    @Value("${my.lock_timeout}")
    public void setLockTimeout(Long value) {
        LockService.lockTimeout = value;
    }

    @Transactional
    public boolean lock(String name) {
        Shedlock shedlock
                = shedlockRepository.findByName(name).orElseThrow();
        if (shedlock.getStatus().equals(ShedlockStatus.IN_PROCESS)
                && (Duration.between(shedlock.getStartTime(), Instant.now()).compareTo(Duration.ofMinutes(lockTimeout)) == -1)) {
            return false;
        } else {
            shedlock.setStatus(ShedlockStatus.IN_PROCESS);
            shedlock.setStartTime(Instant.now());
            return true;
        }

    }

    public void unlock(String name) {
        Optional<Shedlock> byName = shedlockRepository.findByName(name);
        if (byName.isEmpty()) {
            return;
        }
        Shedlock shedlock = byName.get();
        shedlock.setStatus(ShedlockStatus.READY_TO_WORK);
        shedlock.setStartTime(null);
        shedlockRepository.save(shedlock);
    }
}
