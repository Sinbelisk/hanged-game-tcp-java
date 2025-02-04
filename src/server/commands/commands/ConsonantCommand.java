package server.commands.commands;

import server.Worker;
import server.commands.Command;
import server.game.GameRoom;
import server.services.ServiceRegistry;

public class ConsonantCommand implements Command {

    @Override
    public void execute(String[] args, Worker worker) {
        char consonant = args[0].charAt(0);

        GameRoom room = worker.getCurrentRoom();
        if(room == null){
            worker.getMessageService().sendUserNotPlaying();
            return;
        }

        room.guessLetter(worker, consonant, false);
    }

    @Override
    public void assingServices(ServiceRegistry services) {
    }
}