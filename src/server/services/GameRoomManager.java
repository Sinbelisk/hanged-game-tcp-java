package server.services;

import server.game.GameRoom;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class GameRoomManager {

    private ConcurrentMap<String, GameRoom> activeRooms = new ConcurrentHashMap<>();

    public GameRoomManager() {

    }

    public synchronized boolean addRoom(GameRoom room) {
        if(activeRooms.containsKey(room.getName())) {
            return false;
        }

        activeRooms.put(room.getName(), room);
        return true;
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
