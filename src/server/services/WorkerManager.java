package server.services;

import server.Worker;

import java.util.ArrayList;
import java.util.List;

public class WorkerManager {
    private static WorkerManager instance;
    private List<Worker> activeWorkers;

    public WorkerManager() {
        activeWorkers = new ArrayList<>();
    }

    public static WorkerManager getInstance() {
        if(instance == null) {
            instance = new WorkerManager();
        }
        return instance;
    }

    public void addWorker(Worker worker) {
        activeWorkers.add(worker);
    }
}
