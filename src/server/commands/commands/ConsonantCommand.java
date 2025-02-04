package server.commands.commands;

import server.Worker;
import server.game.GameRoom;

public class ConsonantCommand extends AbstractCommand {
    @Override
    public void execute(String[] args, Worker worker) {
        if(!isUserAuthenticated(worker)){
            messageService.sendUserMustAuth(worker);
        }

        char consonant = args[0].charAt(0);

        GameRoom room = worker.getCurrentRoom();
        if(room == null){
            messageService.sendUserNotInGameRoom(worker);
            return;
        }

        room.guessLetter(worker, consonant, false);
    }
}