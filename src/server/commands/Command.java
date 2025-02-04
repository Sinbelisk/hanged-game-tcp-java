package server.commands;

import server.services.ServiceRegistry;
import server.Worker;

/**
 * The Command interface represents a command that can be executed within a chat application.
 * Each command has an associated behavior when executed by the owner (user).
 */
public interface Command {
    /**
     * Executes the command with the provided elements (arguments), client message, and user.
     *
     * @param elements The arguments split from the command string.
     * @param message The message that initiated the command.
     * @param owner The user who executed the command.
     */
    void execute(String[] elements, Worker worker);

    /**
     * Assigns specified services to the command
     * @param services The service registry.
     */
    void setServices(ServiceRegistry services);
}
