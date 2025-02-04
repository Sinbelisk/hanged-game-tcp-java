package server.game;

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

    private int currentTurnIndex = 0;

    public GameRoom(String name, boolean singlePlayer) {
        this.name = name;
        this.necessaryClients = singlePlayer ? SINGLE_PLAYER_CLIENT : DEFAULT_CLIENTS;
        this.clients = Collections.synchronizedList(new ArrayList<>(necessaryClients));
    }

    public synchronized boolean startGame() {
        if (currentGame == null && clients.size() >= necessaryClients) {
            try {
                currentGame = new HangedGame(SayingUtils.getWordsFromDocumentName("seasy"));
                broadcast("[GAME] El juego ha comenzado.");
                showCurrentProverb();
                notifyCurrentTurn();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                broadcast("[ERROR] Error al iniciar la partida.");
            }
        }
        return false;
    }

    public synchronized void addPlayer(Worker w) {
        if (clients.contains(w)) {
            w.getMessageService().send("Ya estás en esta sala.");
            return;
        }
        clients.add(w);
        broadcast("[JOIN] " + w.getUser().getUsername() + " se ha unido a la sala.");
        if (clients.size() >= necessaryClients) {
            startGame();
        } else {
            broadcast("[WAIT] Faltan " + getRemainingPlayers() + " jugadores para comenzar.");
        }
    }

    public synchronized void guessLetter(Worker sender, char letter, boolean isVowel) {
        if (!isPlayerTurn(sender) || currentGame == null) return;

        boolean correct = isVowel ? currentGame.tryVowel(letter) : currentGame.tryConsonant(letter);
        if (!correct && isVowel) sender.getUser().addTry();

        broadcast("[GUESS] " + sender.getUser().getUsername() + " intentó '" + letter + "'. " + (correct ? "[OK] Correcta!" : "[X] Incorrecta."));
        checkGameStatus();
        nextTurn();
    }

    public synchronized void guessPhrase(Worker sender, String phrase) {
        if (!isPlayerTurn(sender) || currentGame == null) return;

        boolean correct = currentGame.tryPhrase(phrase);
        broadcast("[GUESS] " + sender.getUser().getUsername() + " intentó adivinar la frase. " + (correct ? "[WIN] ¡Acertó!" : "[X] Incorrecta."));

        if (correct) {
            sender.getUser().addWin();
            endGame();
        } else {
            sender.getUser().addLoss();
            clients.remove(sender);
            broadcast("[LOSE] " + sender.getUser().getUsername() + " ha perdido la partida.");
            if (clients.size() == 1) {
                broadcast("[WIN] " + clients.get(0).getUser().getUsername() + " ha ganado con " + clients.get(0).getUser().getRoundScore() + " puntos.");
                endGame();
            } else {
                nextTurn();
            }
        }
    }

    private synchronized void checkGameStatus() {
        showCurrentProverb();
        if (currentGame != null && currentGame.isGameCompleted()) {
            broadcast("[WIN] ¡El refrán fue adivinado! Era: " + currentGame.getCurrentSaying().getSaying());
            endGame();
        }
    }

    private synchronized void endGame() {
        List<Worker> clientsCopy = new ArrayList<>(clients); // Copia para evitar modificación concurrente

        for (Worker client : clientsCopy) {
            broadcast("[END] Tu puntuación es: " + client.getUser().getRoundScore());
            client.getUser().addScore(client.getUser().getRoundScore());
            broadcast(client.getUser().getStats());
            broadcast("[END] La sala se va a cerrar. Para jugar otra partida, crea o únete a una nueva.");
            client.getUser().resetTries();
            client.exitRoom();
        }

        clients.clear();
        currentGame = null;
    }


    public synchronized void removePlayer(Worker worker) {
        clients.remove(worker);
        broadcast("[REMOVE] " + worker.getUser().getUsername() + " ha salido de la sala.");

        if (isGameActive() && clients.size() < necessaryClients) {
            broadcast("[REMOVE] No hay jugadores suficientes para continuar la partida.");
        }
    }

    private synchronized void nextTurn() {
        if (clients.size() > 1) {
            currentTurnIndex = (currentTurnIndex + 1) % clients.size();
            notifyCurrentTurn();
        }
    }

    private synchronized void notifyCurrentTurn() {
        if (necessaryClients > 1) {
            broadcast("[TURN] Es el turno de " + clients.get(currentTurnIndex).getUser().getUsername());
        }
    }

    private synchronized boolean isPlayerTurn(Worker sender) {
        if (necessaryClients > 1 && !clients.get(currentTurnIndex).equals(sender)) {
            sender.getMessageService().send("[WAIT] No es tu turno.");
            return false;
        }
        return true;
    }

    private synchronized void showCurrentProverb() {
        broadcast("[PROVERB] Refrán actual: " + currentGame.getCurrentSaying().getHiddenSaying());
    }

    private synchronized void broadcast(String message) {
        clients.forEach(client -> client.getMessageService().send(message));
    }

    public synchronized int getRemainingPlayers() {
        return necessaryClients - clients.size();
    }

    public synchronized boolean isGameActive() {
        return currentGame != null;
    }

    public String getName() {
        return name;
    }

    public boolean isEmpty() {
        return clients.isEmpty();
    }
}