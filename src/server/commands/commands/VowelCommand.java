package server.commands.commands;

import server.Worker;
import server.commands.Command;
import server.game.GameRoom;
import server.services.ServiceRegistry;

public class VowelCommand extends AbstractCommand {

    @Override
    public void execute(String[] args, Worker worker) {
        char vowel = args[0].charAt(0);

        GameRoom room = worker.getCurrentRoom();
        if(room == null){
            messageService.sendUserNotInGameRoom(worker);
            return;
        }

        room.guessLetter(worker, vowel, true);
    }
}