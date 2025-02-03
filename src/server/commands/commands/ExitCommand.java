package server.commands.commands;

import server.services.ServiceRegistry;
import server.Worker;
import server.commands.Command;

public class ExitCommand implements Command {


    @Override
    public void execute(String[] elements, Worker worker) {
        worker.stopWorker();
    }

    @Override
    public void assingServices(ServiceRegistry services) {

    }
}
