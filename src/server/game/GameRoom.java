package server.game;

import server.User;
import server.Worker;
import util.SayingUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

import java.io.IOException;

public class GameRoom {
    private static final int DEFAULT_CLIENTS = 3;
    private static final int SINGLE_PLAYER_CLIENT = 1;

    private final String name;
    private final List<Worker> clients;
    private final int necessaryClients;
    private HangedGame currentGame;


    public GameRoom(String name, Boolean singlePlayer) {
        necessaryClients = singlePlayer ? SINGLE_PLAYER_CLIENT : DEFAULT_CLIENTS;
        this.name = name;
        this.clients = Collections.synchronizedList(new ArrayList<>(necessaryClients));
    }

    public synchronized boolean startGame() {
        if (currentGame == null && clients.size() >= necessaryClients) {
            try {
                currentGame = new HangedGame(SayingUtils.getWordsFromDocumentName("seasy"));
                sendMessageToClients("[GAME] El juego ha comenzado.");
                showCurrentProverb();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                sendMessageToClients("[ERROR] Error al iniciar la partida.");
            }
        }
        return false;
    }

    public synchronized void stopGame() {
        if (currentGame != null) {
            sendMessageToClients("[STOP] La partida ha terminado.");
            currentGame = null;
        }
    }

    public synchronized void addPlayer(Worker w) {
        if (clients.contains(w)) {
            w.getMessageService().send("Ya estás en esta sala.");
            return;
        }

        clients.add(w);
        notifyPlayerJoin(w);

        if (clients.size() >= necessaryClients) {
            startGame();
        } else {
            showRemainingPlayers();
        }
    }

    public synchronized void guessConsonant(Worker sender, char consonant) {
        if (currentGame != null) {
            boolean correct = currentGame.tryConsonant(consonant);
            sendMessageToClients("[GUESS] " + sender.getUser().getUsername() + " ha intentado la consonante '" + consonant + "'. " + (correct ? "[OK] Correcta!" : "[X] Incorrecta."));
            checkGameStatus();
        }
    }

    public synchronized void guessVowel(Worker sender, char vowel) {
        if (currentGame != null) {
            boolean correct = currentGame.tryVowel(vowel);
            if (!correct) {
                sender.getUser().addTry();
            }
            sendMessageToClients("[GUESS] " + sender.getUser().getUsername() + " ha intentado la vocal '" + vowel + "'. " + (correct ? "[OK] Correcta!" : "[X] Incorrecta."));
            checkGameStatus();
        }
    }

    public synchronized void guessPhrase(Worker sender, String phrase) {
        if (currentGame != null) {
            boolean correct = currentGame.tryPhrase(phrase);
            sendMessageToClients("[GUESS] " + sender.getUser().getUsername() + " ha intentado adivinar la frase. " + (correct ? "[WIN] ¡Acertó!" : "[X] Incorrecta."));
            checkGameStatus();
        }
    }

    private synchronized void checkGameStatus() {
        showCurrentProverb();
        if (currentGame != null && currentGame.isGameCompleted()) {
            sendMessageToClients("[WIN] ¡El refrán fue adivinado! Era: " + currentGame.getCurrentSaying().getSaying());
            gameEnded();
        }
    }

    // TODO: Hacer si queda tiempo
    private synchronized void nextRound() {
        try{
            if (!currentGame.getCurrentSaying().isWordCompleted()) {
                sendMessageToClients("[NEXT] Siguiente refrán...");
                currentGame = new HangedGame();
                sendMessageToClients("[GAME] Nuevo refrán: " + currentGame.getCurrentSaying().getHiddenSaying());
            } else {
                stopGame();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void notifyPlayerJoin(Worker newPlayer) {
        for (Worker client : clients) {
            if (!client.equals(newPlayer)) {
                client.getMessageService().send("[JOIN] " + newPlayer.getUser().getUsername() + " se ha unido a la sala.");
            }
        }
    }

    public synchronized int getRemainingPlayers() {
        return necessaryClients - clients.size();
    }

    public synchronized List<Worker> getClients() {
        return new ArrayList<>(clients);
    }

    public synchronized boolean isGameActive() {
        return currentGame != null;
    }

    public HangedGame getCurrentGame() {
        return currentGame;
    }

    public String getName() {
        return name;
    }

    public boolean isEmpty() {
        return clients.isEmpty();
    }

    private synchronized void showRemainingPlayers() {
        sendMessageToClients("[WAIT] Faltan " + getRemainingPlayers() + " jugadores para comenzar.");
    }

    private synchronized void showCurrentProverb(){
        sendMessageToClients("[PROVERB] Refrán actual: " + currentGame.getCurrentSaying().getHiddenSaying());
    }

    private synchronized void sendMessageToClients(String message) {
        for (Worker client : clients) {
            client.getMessageService().send(message);
        }
    }

    private synchronized void gameEnded(){
        for (Worker client : clients) {
           sendMessageToClients("[END] El juego ha terminado, tu puntuación es:" + client.getUser().getScore());
           sendMessageToClients("[END] Crea o únete a otra sala para jugar otra partida.");
           client.exitRoom();
        }

        clients.clear();
    }

    public int getNecessaryClients() {
        return necessaryClients;
    }

    public synchronized void removePlayer(Worker worker) {
        clients.remove(worker);

        User user = worker.getUser();
        if(isGameActive() && clients.size() < necessaryClients){
            sendMessageToClients("[REMOVE] El usuario " + user.getUsername() + " ha salido de la sala, no hay jugadores suficientes para continuar la partida" );
        }
        else if (!isGameActive() && clients.size() < necessaryClients){
            sendMessageToClients("[REMOVE] El usuario " + user.getUsername() + " ha salido de la sala.");
            showRemainingPlayers();
        }
    }
}