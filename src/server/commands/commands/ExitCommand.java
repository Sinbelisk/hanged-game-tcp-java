package server.commands.commands;

import server.services.ServiceRegistry;
import server.Worker;
import server.commands.Command;

public class ExitCommand extends AbstractCommand {

    @Override
    public void execute(String[] elements, Worker worker) {
        worker.stopWorker();
        messageService.send("Se ha terminado la conexión con el servidor.", worker);
    }
}
