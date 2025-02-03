package server.services;

import server.Worker;

import java.util.ArrayList;
import java.util.List;

public class WorkerManager {
    private final List<Worker> activeWorkers;

    public WorkerManager() {
        activeWorkers = new ArrayList<>();
    }

    public void addWorker(Worker worker) {
        activeWorkers.add(worker);
    }

    public void removeWorker(Worker worker) {
        activeWorkers.remove(worker);
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
}
