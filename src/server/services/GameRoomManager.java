package server.services;

import server.game.GameRoom;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class GameRoomManager {

    private final ConcurrentMap<String, GameRoom> activeRooms = new ConcurrentHashMap<>();

    public GameRoomManager() {

    }

    public synchronized void addRoom(GameRoom room) {
        if(activeRooms.containsKey(room.getName())) {
            return;
        }

        activeRooms.put(room.getName(), room);
    }

    public synchronized void checkAndStartGame(String roomName){
        GameRoom room = activeRooms.get(roomName);

        if (room != null && !room.isGameActive() && room.getRemainingPlayers() == 0) {
            room.startGame();
        }
    }

    public synchronized boolean removeRoom(String roomName) {
        if(activeRooms.containsKey(roomName)) {
            activeRooms.remove(roomName);
            return true;
        }
        return false;
    }

    public synchronized GameRoom getRoom(String roomName) {
        return activeRooms.get(roomName);
    }
}
