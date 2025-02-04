package server.services;

import server.game.GameRoom;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Gestor de salas de juego en el servidor.
 * <p>
 * Esta clase administra las salas de juego activas, permitiendo agregar,
 * eliminar y gestionar su estado. También se encarga de verificar si una
 * sala está lista para iniciar una partida.
 * </p>
 */
public class GameRoomManager {

    private final ConcurrentMap<String, GameRoom> activeRooms = new ConcurrentHashMap<>();

    /**
     * Constructor por defecto de {@code GameRoomManager}.
     */
    public GameRoomManager() {
    }

    /**
     * Agrega una nueva sala de juego si aún no está registrada.
     *
     * @param room La sala de juego a agregar.
     */
    public synchronized void addRoom(GameRoom room) {
        if (activeRooms.containsKey(room.getName())) {
            return;
        }
        activeRooms.put(room.getName(), room);
    }

    /**
     * Verifica si una sala de juego tiene suficientes jugadores para iniciar la partida
     * e inicia el juego si es necesario.
     *
     * @param roomName Nombre de la sala a comprobar.
     */
    public synchronized void checkAndStartGame(String roomName) {
        GameRoom room = activeRooms.get(roomName);

        if (room != null && !room.isGameActive() && room.getRemainingPlayers() == 0) {
            room.startGame();
        }
    }

    /**
     * Elimina una sala de juego de la lista de salas activas si existe.
     *
     * @param roomName Nombre de la sala a eliminar.
     * @return {@code true} si la sala fue eliminada, {@code false} si no existía.
     */
    public synchronized boolean removeRoom(String roomName) {
        if (activeRooms.containsKey(roomName)) {
            activeRooms.remove(roomName);
            return true;
        }
        return false;
    }

    /**
     * Obtiene una sala de juego por su nombre.
     *
     * @param roomName Nombre de la sala a buscar.
     * @return La sala de juego si existe, {@code null} en caso contrario.
     */
    public synchronized GameRoom getRoom(String roomName) {
        return activeRooms.get(roomName);
    }
}
