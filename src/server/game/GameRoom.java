package server.game;

import server.Worker;
import server.game.model.HangedGame;
import server.services.MessageService;
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

    private final MessageService messageService;

    public GameRoom(String name, boolean singlePlayer, MessageService messageService) {
        this.name = name;
        this.necessaryClients = singlePlayer ? SINGLE_PLAYER_CLIENT : DEFAULT_CLIENTS;
        this.clients = Collections.synchronizedList(new ArrayList<>(necessaryClients));
        this.messageService = messageService;
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

    public synchronized void addPlayer(Worker client) {
        if (clients.contains(client)) {
            messageService.send("Ya estás en esta sala.", client);
            return;
        }
        clients.add(client);
        broadcast("[JOIN] " + client.getUser().getUsername() + " se ha unido a la sala.");
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

    // El bug ocurre porque el codigo de verificacion que se activa al salir un jugador se activa cada vez que sale
    // un jugador.

    public synchronized void removePlayer(Worker worker) {
        clients.remove(worker);
        worker.getUser().resetTries();
        broadcast("[REMOVE] " + worker.getUser().getUsername() + " ha salido de la sala.");

        if (clients.isEmpty()) {
            endGame();
        } else if (isGameActive() && clients.size() < necessaryClients) {
            broadcast("[REMOVE] No hay jugadores suficientes para continuar la partida.");
            endGame();
        }
    }

    private synchronized void endGame() {
        clients.forEach(client -> {
            broadcast("[END] Tu puntuación es: " + client.getUser().getRoundScore());
            client.getUser().addScore(client.getUser().getRoundScore());
            broadcast(client.getUser().getStats());
            broadcast("[END] La sala se va a cerrar. Para jugar otra partida, crea o únete a una nueva.");
            client.exitRoom();
        });

        clients.clear();
        currentGame = null;
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
            messageService.send("[WAIT] No es tu turno.", sender);
            return false;
        }
        return true;
    }

    private synchronized void showCurrentProverb() {
        broadcast("[PROVERB] Refrán actual: " + currentGame.getCurrentSaying().getHiddenSaying());
    }

    private synchronized void broadcast(String message) {
        clients.forEach(client -> {messageService.send(message, client);});
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