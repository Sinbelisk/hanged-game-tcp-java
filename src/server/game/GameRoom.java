package server.game;

import server.Worker;
import server.game.model.HangedGame;
import server.services.MessageService;
import util.SayingUtils;

import java.util.Comparator;
import java.util.List;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;


public class GameRoom {
    private static final int DEFAULT_CLIENTS = 3;
    private static final int SINGLE_PLAYER_CLIENT = 1;

    private final String name;
    private final List<Worker> clients;
    private final ConcurrentMap<Worker, ScoreManager> scoreMap;
    private final int necessaryClients;
    private final MessageService messageService;

    private HangedGame currentGame;
    private int currentTurnIndex = 0;

    private User winner;

    public GameRoom(String name, boolean singlePlayer, MessageService messageService) {
        this.name = name;
        this.necessaryClients = singlePlayer ? SINGLE_PLAYER_CLIENT : DEFAULT_CLIENTS;
        this.clients = new CopyOnWriteArrayList<>();
        this.messageService = messageService;
        this.scoreMap = new ConcurrentHashMap<>();
    }

    public synchronized void startGame() {
        if (currentGame == null && clients.size() >= necessaryClients) {
            try {
                currentGame = new HangedGame(SayingUtils.getWordsFromDocumentName("seasy"));
                broadcast("[GAME] El juego ha comenzado.");
                updateGameState();
            } catch (IOException e) {
                broadcast("[ERROR] Error al iniciar la partida: " + e.getMessage());
            }
        }
    }

    public synchronized void addPlayer(Worker client) {
        if (!clients.contains(client)) {
            clients.add(client);
            scoreMap.put(client, new ScoreManager());

            broadcastExclude("[JOIN] " + client.getUser().getUsername() + " se ha unido a la sala.", client);
            checkStartConditions();
        } else {
            messageService.send("Ya estás en esta sala.", client);
        }
    }

    public synchronized void guessLetter(Worker sender, char letter, boolean isVowel) {
        if (!isGameReady(sender)) return;

        boolean correct = isVowel ? currentGame.tryVowel(letter) : currentGame.tryConsonant(letter);
        if (!correct && isVowel) scoreMap.get(sender).addTry();

        String message = "[GUESS] " + sender.getUser().getUsername() + " intentó '" + letter + "'. " +
                (correct ? "[OK] Correcta!" : "[X] Incorrecta.");
        broadcast(message);
        updateGameState();
    }

    public synchronized void guessPhrase(Worker sender, String phrase) {
        if (!isGameReady(sender)) return;

        boolean correct = currentGame.tryPhrase(phrase);
        String message = "[GUESS] " + sender.getUser().getUsername() + " intentó adivinar la frase. " +
                (correct ? "[WIN] ¡Acertó!" : "[X] Incorrecta.");

        if (correct) {
            sender.getUser().addWin();
            winner = sender.getUser();
            announceWinner(sender);
        } else {
            handleLoss();
        }

        broadcast(message);
    }

    private synchronized void handleLoss() {
        Worker highestScoreUser = getWinnerWithHighestScore();

        clients.forEach(client -> {
            if (!client.equals(highestScoreUser)) {
                client.getUser().addLoss();
            }
        });

        announceWinner(highestScoreUser);
    }

    private Worker getWinnerWithHighestScore() {
        return scoreMap.entrySet().stream()
                .max(Comparator.comparingInt(entry -> entry.getValue().getRoundScore()))
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    private synchronized void updateGameState() {
        if (currentGame == null) return;

        if (currentGame.isGameCompleted()) {
            broadcast("[WIN] ¡El refrán fue adivinado! Era: " + currentGame.getCurrentSaying().getSaying());
            endGame();
        } else {
            showCurrentProverb();
            nextTurn();
        }
    }

    private synchronized boolean isGameReady(Worker sender) {
        return isPlayerTurn(sender) && currentGame != null;
    }

    private synchronized void announceWinner(Worker winner) {
        clients.forEach(client -> {
            String message = client.equals(winner)
                    ? "[WIN] ¡Has ganado con " + scoreMap.get(winner).getRoundScore() + " puntos!"
                    : "[WIN] " + winner.getUser().getUsername() + " ha ganado con " + scoreMap.get(winner).getRoundScore() + " puntos.";
            messageService.send(message, client);
        });
        endGame();
    }

    public synchronized void removePlayer(Worker worker) {
        clients.remove(worker);
        scoreMap.remove(worker);
        broadcast("[REMOVE] " + worker.getUser().getUsername() + " ha salido de la sala.");

        if (clients.size() < necessaryClients && currentGame != null) {
            broadcast("[REMOVE] No hay jugadores suficientes para continuar la partida.");
            endGame();
        }
    }

    private synchronized void endGame() {
        currentGame = null;
        currentTurnIndex = 0;

        for (Worker client : clients) {
            int score = (client.getUser() == winner) ? scoreMap.get(client).getRoundScore() : 0;

            if(client.getUser() != winner) broadcast("[END] Tu puntuación es: " + score);

            client.getUser().sumScore(score);
            broadcast(client.getUser().getStats());

            client.exitRoom();
        }

        clients.clear();
    }

    private synchronized void nextTurn() {
        if (clients.size() > 1) {
            currentTurnIndex = (currentTurnIndex + 1) % clients.size();
            notifyCurrentTurn();
        }
    }

    private synchronized void notifyCurrentTurn() {
        Worker currentPlayer = clients.get(currentTurnIndex);
        clients.forEach(client -> {
            String message = client.equals(currentPlayer)
                    ? "[TURN] Es tu turno."
                    : "[TURN] Es el turno de " + currentPlayer.getUser().getUsername();
            messageService.send(message, client);
        });
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
        clients.forEach(client -> messageService.send(message, client));
    }

    private synchronized void broadcastExclude(String message, Worker excluded) {
        clients.stream()
                .filter(clients -> clients.equals(excluded))
                .forEach(client -> messageService.send(message, client));
    }

    private synchronized void checkStartConditions() {
        if (clients.size() >= necessaryClients) {
            startGame();
        } else {
            broadcast("[WAIT] Faltan " + getRemainingPlayers() + " jugadores para comenzar.");
        }
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