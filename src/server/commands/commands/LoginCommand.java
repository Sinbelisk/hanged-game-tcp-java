package server.commands.commands;

import server.User;
import server.UserManager;
import server.Worker;
import server.commands.Command;

public class LoginCommand implements Command {
    private final UserManager userManager;

    public LoginCommand() {
        userManager = UserManager.getInstance();
    }
    @Override
    public void execute(String[] elements, Worker worker) {
        String username = elements[0];
        String password = elements[1];

        User user = userManager.getUser(username);

        if(user.getPassword().equals(password)) {
            user.setAutentified(true);
        }
    }
}
