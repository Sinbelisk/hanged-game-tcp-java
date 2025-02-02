package server.commands.commands;

import server.User;
import server.UserManager;
import server.Worker;
import server.commands.Command;

public class LoginCommand implements Command {

    public LoginCommand() {
    }
    @Override
    public void execute(String[] elements, Worker worker) {
        String username = elements[0];
        String password = elements[1];

        User user = UserManager.getInstance().getUser(username);

        if(user.getPassword().equals(password)) {
            user.setAutentified(true);
            System.out.printf("Usuario autentifikao");
        }
    }
}
