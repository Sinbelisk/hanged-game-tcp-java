package server.services;

import common.Connection;
import server.User;
import util.SimpleLogger;

import java.util.logging.Logger;

public class MessageService {
    private static final Logger logger = SimpleLogger.getInstance().getLogger(MessageService.class);
    private final Connection connection;
    private final User user;

    public MessageService(Connection connection, User user) {
        this.connection = connection;
        this.user = user;
    }

    public void sendUnknownCommand() {
        send("Comando desconocido, usa '/help' para ver los comandos disponibles");
    }

    public void sendUserHasLogedIn() {
        send("Te has conectado al servidor, usa '/help' para ver los comandos disponibles");
    }

    public void sendUnknownCredentials() {
        send("Credenciales inválidas, intenta de nuevo");
    }

    public void sendUserMustRegister() {
        send("Debes registrarte para acceder al servidor");
    }

    public void sendUserHasRegistered() {
        send("Usuario registrado, usa '/login <usuario> <contraseña>' para iniciar sesión.");
    }

    public void sendPlayerCurrentlyPlaying() {
        send("Ya estás en una sala de juego.");
    }

    public void sedUserCouldntRegister() {
        send("Ese nombre de usuario no está disponible, prueba con otro");
    }

    public void sendUserNotPlaying() {
        send("No estás en ninguna partida activa.");
    }

    public void sendUserExittedRoom(){
        send("Has abandonado la sala.");
    }

    public void send(String msg) {
        try {
            String userId = user == null ? "" : user.getId();
            logger.info("Enviando mensaje a " + userId + ": " + msg);
            connection.send(msg);
        } catch (Exception e) {
            logger.severe("Error al enviar mensaje: " + e.getMessage());
        }
    }
}
