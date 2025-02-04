package server.commands.commands;

import server.services.ServiceRegistry;
import server.Worker;
import server.commands.Command;
import server.services.WorkerManager;

public class ExitCommand implements Command {
    private WorkerManager manager;

    @Override
    public void execute(String[] elements, Worker worker) {
        manager.removeWorker(worker);

        worker.stopWorker();
        worker.getMessageService().sendUserLogOut();
    }

    @Override
    public void assingServices(ServiceRegistry services) {
        services.getService(WorkerManager.class);
    }
}
