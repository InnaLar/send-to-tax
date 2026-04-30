package larina.lessons.send_to_tax.services;

import jakarta.transaction.Transactional;
import larina.lessons.send_to_tax.model.entity.Shedlock;
import larina.lessons.send_to_tax.model.entity.ShedlockStatus;
import larina.lessons.send_to_tax.repository.ShedlockRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class LockService {
    private final ShedlockRepository shedlockRepository;

    @Transactional
    public boolean lock(String name) {
        Shedlock shedlock
                = shedlockRepository.findByName(name).orElseThrow();

        if (shedlock.getStatus().equals(ShedlockStatus.IN_PROCESS)) {
            return true;
        } else {
            shedlock.setStatus(ShedlockStatus.IN_PROCESS);
            return false;
        }

    }

    public void unlock(String name) {
        Optional<Shedlock> byName = shedlockRepository.findByName(name);
        if (byName.isEmpty()) {
            return;
        }
        Shedlock shedlock = byName.get();
        shedlock.setStatus(ShedlockStatus.READY_TO_WORK);
        shedlockRepository.save(shedlock);
    }
}
