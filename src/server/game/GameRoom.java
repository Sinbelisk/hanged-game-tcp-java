package server.game;

import server.Worker;
import server.game.model.HangedGame;
import server.services.MessageService;
import server.util.SayingUtils;

import java.util.Comparator;
import java.util.List;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Representa una sala de juego donde varios jugadores pueden unirse para jugar al juego del ahorcado.
 * TODO: Refactorizar este monstro.
 */
public class GameRoom {
    /**
     * Número de jugadores por defecto necesarios para comenzar el juego.
     */
    private static final int DEFAULT_CLIENTS = 3;

    /**
     * Número de jugadores necesarios para el modo de un solo jugador.
     */
    private static final int SINGLE_PLAYER_CLIENT = 1;

    /**
     * Nombre de la sala de juego.
     */
    private final String name;

    /**
     * Lista de los jugadores (clientes) presentes en la sala.
     */
    private final List<Worker> clients;

    /**
     * Mapa que asocia a cada jugador un objeto ScoreManager que lleva el puntaje del jugador.
     */
    private final ConcurrentMap<Worker, ScoreManager> scoreMap;

    /**
     * Número de jugadores necesarios para iniciar la partida.
     */
    private final int necessaryClients;

    /**
     * Servicio encargado de enviar mensajes a los jugadores.
     */
    private final MessageService messageService;

    /**
     * Objeto que representa el juego actual.
     */
    private HangedGame currentGame;

    /**
     * Índice del jugador actual en el turno del juego.
     */
    private int currentTurnIndex = 0;

    /**
     * El jugador que ha ganado la partida.
     */
    private User winner;

    /**
     * Constructor que inicializa una nueva sala de juego.
     * @param name Nombre de la sala de juego.
     * @param singlePlayer Determina si es un juego para un solo jugador.
     * @param messageService Servicio para enviar mensajes a los jugadores.
     */
    public GameRoom(String name, boolean singlePlayer, MessageService messageService) {
        this.name = name;
        this.necessaryClients = singlePlayer ? SINGLE_PLAYER_CLIENT : DEFAULT_CLIENTS;
        this.clients = new CopyOnWriteArrayList<>();
        this.messageService = messageService;
        this.scoreMap = new ConcurrentHashMap<>();
    }

    /**
     * Inicia el juego si hay suficientes jugadores y el juego no ha comenzado aún.
     */
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

    /**
     * Agrega un jugador a la sala si no está ya en ella.
     * @param client El jugador que se unirá a la sala.
     */
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

    /**
     * Permite a un jugador adivinar una letra del juego.
     * @param sender El jugador que hace la adivinanza.
     * @param letter La letra adivinada.
     * @param isVowel Indica si la letra es una vocal.
     */
    public synchronized void guessLetter(Worker sender, char letter, boolean isVowel) {
        if (!isGameReady(sender)) return;

        boolean correct = isVowel ? currentGame.tryVowel(letter) : currentGame.tryConsonant(letter);
        if (!correct && isVowel) scoreMap.get(sender).addTry();

        String message = "[GUESS] " + sender.getUser().getUsername() + " intentó '" + letter + "'. " +
                (correct ? "[OK] Correcta!" : "[X] Incorrecta.");
        broadcast(message);
        updateGameState();
    }

    /**
     * Permite a un jugador adivinar la frase completa.
     * @param sender El jugador que hace la adivinanza.
     * @param phrase La frase que el jugador intenta adivinar.
     */
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

    /**
     * Maneja la lógica cuando un jugador pierde, y elige al jugador con más puntos como ganador.
     */
    private synchronized void handleLoss() {
        Worker highestScoreUser = getWinnerWithHighestScore();

        clients.forEach(client -> {
            if (!client.equals(highestScoreUser)) {
                client.getUser().addLoss();
            }
        });

        announceWinner(highestScoreUser);
    }

    /**
     * Obtiene el jugador con el puntaje más alto.
     * @return El jugador con el puntaje más alto.
     */
    private Worker getWinnerWithHighestScore() {
        return scoreMap.entrySet().stream()
                .max(Comparator.comparingInt(entry -> entry.getValue().getRoundScore()))
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    /**
     * Actualiza el estado del juego, mostrando el proverbio y pasando al siguiente turno si es necesario.
     */
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

    /**
     * Verifica si el juego está listo para que un jugador realice una acción.
     * @param sender El jugador que intenta realizar la acción.
     * @return true si el juego está listo, false en caso contrario.
     */
    private synchronized boolean isGameReady(Worker sender) {
        return isPlayerTurn(sender) && currentGame != null;
    }

    /**
     * Anuncia al ganador de la partida a todos los jugadores.
     * @param winner El jugador que ganó la partida.
     */
    private synchronized void announceWinner(Worker winner) {
        clients.forEach(client -> {
            String message = client.equals(winner)
                    ? "[WIN] ¡Has ganado con " + scoreMap.get(winner).getRoundScore() + " puntos!"
                    : "[WIN] " + winner.getUser().getUsername() + " ha ganado con " + scoreMap.get(winner).getRoundScore() + " puntos.";
            messageService.send(message, client);
        });
        endGame();
    }

    /**
     * Elimina a un jugador de la sala de juego.
     * @param worker El jugador que se va a eliminar.
     */
    public synchronized void removePlayer(Worker worker) {
        clients.remove(worker);
        scoreMap.remove(worker);

        broadcast("[REMOVE] " + worker.getUser().getUsername() + " ha salido de la sala.");

        if (clients.size() < necessaryClients && currentGame != null) {
            broadcast("[REMOVE] No hay jugadores suficientes para continuar la partida.");
            endGame();
        }
    }

    /**
     * Finaliza el juego, reparte los puntos y limpia la sala.
     */
    private synchronized void endGame() {
        currentGame = null;
        currentTurnIndex = 0;

        for (Worker client : clients) {
            int score = (client.getUser() == winner) ? scoreMap.get(client).getRoundScore() : 0;

            if(client.getUser() != winner) broadcast("[END] Tu puntuación es: " + score);

            client.getUser().sumScore(score);
            messageService.send(client.getUser().getStats(), client);

            client.exitRoom();
        }

        clients.clear();
    }

    /**
     * Avanza al siguiente turno en el juego.
     */
    private synchronized void nextTurn() {
        if (clients.size() > 1) {
            currentTurnIndex = (currentTurnIndex + 1) % clients.size();
            notifyCurrentTurn();
        }
    }

    /**
     * Notifica a todos los jugadores quién tiene el turno actual.
     */
    private synchronized void notifyCurrentTurn() {
        Worker currentPlayer = clients.get(currentTurnIndex);
        clients.forEach(client -> {
            String message = client.equals(currentPlayer)
                    ? "[TURN] Es tu turno."
                    : "[TURN] Es el turno de " + currentPlayer.getUser().getUsername();
            messageService.send(message, client);
        });
    }

    /**
     * Verifica si es el turno del jugador para realizar una acción.
     * @param sender El jugador que intenta realizar la acción.
     * @return true si es su turno, false en caso contrario.
     */
    private synchronized boolean isPlayerTurn(Worker sender) {
        if (necessaryClients > 1 && !clients.get(currentTurnIndex).equals(sender)) {
            messageService.send("[WAIT] No es tu turno.", sender);
            return false;
        }
        return true;
    }

    /**
     * Muestra el proverbio actual del juego a todos los jugadores.
     */
    private synchronized void showCurrentProverb() {
        broadcast("[PROVERB] Refrán actual: " + currentGame.getCurrentSaying().getHiddenSaying());
    }

    /**
     * Envía un mensaje a todos los jugadores en la sala.
     * @param message El mensaje que se enviará.
     */
    private synchronized void broadcast(String message) {
        clients.forEach(client -> messageService.send(message, client));
    }

    /**
     * Envía un mensaje a todos los jugadores excepto al excluido.
     * @param message El mensaje que se enviará.
     * @param excluded El jugador que no recibirá el mensaje.
     */
    private synchronized void broadcastExclude(String message, Worker excluded) {
        clients.stream()
                .filter(client -> !client.equals(excluded))
                .forEach(client -> messageService.send(message, client));
    }

    /**
     * Verifica si hay suficientes jugadores para iniciar el juego.
     */
    private synchronized void checkStartConditions() {
        if (clients.size() >= necessaryClients) {
            startGame();
        } else {
            broadcast("[WAIT] Faltan " + getRemainingPlayers() + " jugadores para comenzar.");
        }
    }

    /**
     * Obtiene la cantidad de jugadores restantes necesarios para comenzar el juego.
     * @return El número de jugadores que faltan.
     */
    public synchronized int getRemainingPlayers() {
        return necessaryClients - clients.size();
    }

    /**
     * Verifica si el juego está activo.
     * @return true si el juego está activo, false en caso contrario.
     */
    public synchronized boolean isGameActive() {
        return currentGame != null;
    }

    /**
     * Obtiene el nombre de la sala.
     * @return El nombre de la sala.
     */
    public String getName() {
        return name;
    }

    /**
     * Verifica si la sala está vacía.
     * @return true si la sala está vacía, false en caso contrario.
     */
    public boolean isEmpty() {
        return clients.isEmpty();
    }
}
