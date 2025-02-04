package server.services;

import server.Worker;
import util.SimpleLogger;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

public class WorkerManager {
    private static final Logger log = SimpleLogger.getInstance().getLogger(WorkerManager.class);
    private final List<Worker> activeWorkers;

    public WorkerManager() {
        activeWorkers = new ArrayList<>();
    }

    public void addWorker(Worker worker) {
        activeWorkers.add(worker);
        log.info("Worker added: " + worker);
    }

    public void removeWorker(Worker worker) {
        activeWorkers.remove(worker);
        log.info("Worker removed: " + worker);
    }

    public void removeAllWorkers() {
        activeWorkers.clear();
    }

    public Worker getWorker(String name) {
        for(Worker worker : activeWorkers) {
            if(worker.getName().equals(name)) {
                return worker;
            }
        }
        return null;
    }

    public void healthCheck() {
        Iterator<Worker> iterator = activeWorkers.iterator();
        while (iterator.hasNext()) {
            Worker worker = iterator.next();
            log.info("Worker health check: " + worker + "| [" + worker.getState() + "]");

            if (worker.isInterrupted()) {
                log.info("Worker " + worker.getName() + " is interrupted");
                iterator.remove();
            }
        }
    }

}
