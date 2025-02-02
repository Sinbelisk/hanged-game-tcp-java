package server.commands.commands;

import server.UserManager;
import server.Worker;
import server.commands.Command;

public class RegisterCommand implements Command {

    public RegisterCommand() {
    }

    @Override
    public void execute(String[] elements, Worker worker) {
        if (elements.length < 2) {
            return;
        }

        String name = elements[0];
        String password = elements[1];

        UserManager.getInstance().registerUser(name, password);
    }
}
