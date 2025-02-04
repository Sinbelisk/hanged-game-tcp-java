package server.commands.commands;

import server.Worker;
import server.commands.Command;
import server.game.GameRoom;
import server.services.ServiceRegistry;

public class PhraseCommand extends AbstractCommand {

    @Override
    public void execute(String[] args, Worker worker) {
        String phrase = String.join(" ", args);

        GameRoom room = worker.getCurrentRoom();
        if(room == null){
            messageService.sendUserNotInGameRoom(worker);
            return;
        }

        room.guessPhrase(worker, phrase);
    }
}