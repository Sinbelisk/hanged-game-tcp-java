package server.commands.commands;

import server.Worker;
import server.commands.Command;
import server.game.User;
import server.services.MessageService;
import server.services.ServiceRegistry;

/**
 * La clase {@code AbstractCommand} implementa la interfaz {@link Command} y proporciona funcionalidades comunes
 * para los comandos del juego, incluyendo el acceso al {@link MessageService} y un método para verificar la autenticación del usuario.
 */
public abstract class AbstractCommand implements Command {

    /** El servicio de mensajes utilizado para enviar mensajes a los clientes. */
    protected MessageService messageService;

    /**
     * Establece los servicios requeridos por el comando. Esta implementación obtiene el {@link MessageService}
     * del registro de servicios proporcionado y lo asigna al campo {@code messageService}.
     *
     * @param services El registro de servicios que contiene los servicios necesarios para el comando.
     */
    @Override
    public void setServices(ServiceRegistry services) {
        messageService = services.getService(MessageService.class);
    }

    /**
     * Verifica si el usuario asociado con el trabajador está autenticado.
     *
     * @param worker El objeto {@link Worker} que representa al usuario.
     * @return {@code true} si el usuario está autenticado, {@code false} en caso contrario.
     */
    protected boolean isUserAuthenticated(Worker worker) {
        User user = worker.getUser();

        // Si no hay un usuario o el usuario no está autenticado, se devuelve false
        if (user == null) {
            return false;
        } else {
            return user.isAuthenticated();
        }
    }
}
