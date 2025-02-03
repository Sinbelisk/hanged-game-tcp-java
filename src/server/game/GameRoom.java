package server.game;

import server.User;
import server.Worker;

import java.util.ArrayList;
import java.util.List;

public class GameRoom {
    private static final int DEFAULT_CLIENTS = 3;
    private static final int SINGLE_PLAYER_CLIENT = 1;

    private final String name;
    private final List<Worker> clients;
    private final boolean singlePlayer;
    private final int necessaryClients;
    private HangedGame currentGame;

    public GameRoom(String name, Boolean singlePlayer) {
        necessaryClients = singlePlayer? SINGLE_PLAYER_CLIENT : DEFAULT_CLIENTS;

        this.name = name;
        clients = singlePlayer ? new ArrayList<>(SINGLE_PLAYER_CLIENT) : new ArrayList<>(DEFAULT_CLIENTS);
        this.singlePlayer = singlePlayer;
    }

    public boolean startGame(){
        try{
            if(currentGame != null){
                currentGame = new HangedGame();
                return true;
            }
        } catch (Exception e){
            e.printStackTrace();

        }
        return false;
    }

    public void stopGame(){
        this.currentGame = null;
    }

    public int getRemainingPlayers() {
        return necessaryClients - clients.size();
    }

    public void addPlayer(Worker w) {
        clients.add(w);

        if(!singlePlayer){
            for (Worker client : clients) {
                if(!client.equals(w)){
                    client.getMessageService().send(client.getUser().getUsername() + " ha entrado en la sala.");
                }
            }
        }

        showRemainingPlayers();
    }

     void showRemainingPlayers() {
        for (Worker client : clients) {
            client.getMessageService().send("Jugadores restantes: " + getRemainingPlayers());
        }
    }

    public int getNecessaryClients() {
        return necessaryClients;
    }

    public List<Worker> getClients() {
        return clients;
    }

    public List<User> getUsers() {
        List<User> users = new ArrayList<>(clients.size());
        for (Worker client : clients) {
            users.add(client.getUser());
        }

        return users;
    }

    public boolean isSinglePlayer() {
        return singlePlayer;
    }

    public boolean isGameActive() {
        return currentGame != null;
    }

    public HangedGame getCurrentGame() {
        return currentGame;
    }

    public String getName() {
        return name;
    }
}
