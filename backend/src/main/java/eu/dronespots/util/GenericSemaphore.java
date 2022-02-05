package eu.dronespots.util;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class GenericSemaphore {

	Logger logger = LoggerFactory.getLogger(GenericSemaphore.class);

	private Map<String, Semaphore> semaphoreMap = new HashMap<String, Semaphore>();

	public boolean tryAquireLock(String lockId) throws InterruptedException {
		Semaphore semaphore = semaphoreMap.get(lockId);
		if (semaphore == null) {
			semaphore = new Semaphore(1);
			semaphoreMap.put(lockId, semaphore);
		}
		return semaphore.tryAcquire(5, TimeUnit.SECONDS);
	}

	public void releaseLock(String lockId) {
		Semaphore semaphore = semaphoreMap.get(lockId);
		if (semaphore == null) {
			logger.error("ERROR: Tried to release lock for '{}', but it didn't exist.", lockId);
			return;
		}
		if (semaphore.availablePermits() > 0) {
			logger.error(
					"ERROR: There were permits for lock '{}' available although there shouldn't. Draining them for robustness, but this should NOT happen.",
					lockId);
			semaphore.drainPermits();
		}
		semaphore.release();
	}
}
