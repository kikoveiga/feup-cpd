import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import game_logic.TriviaResponse;
import game_logic.TriviaResult;

public class Game {
    private List<Client> playerList;
    private final Lock playerList_lock = new ReentrantLock();
    private TriviaResponse triviaResponse;
    private volatile boolean isGameRunning;
    private final int ROUNDS = 4;
    private ExecutorService playerThreadPool;

    public Game(List<Client> playerList) {
        this.playerList = playerList;
        this.triviaResponse = new TriviaResponse();
        this.isGameRunning = false;
        this.playerThreadPool = Executors.newVirtualThreadPerTaskExecutor();
    }

    public void loadQuestions(String dataPath) {
        File jsonFile = new File(dataPath);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            triviaResponse = objectMapper.readValue(jsonFile, new TypeReference<TriviaResponse>() {});
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startGame() {
        loadQuestions("src/database/questions.json");
        isGameRunning = true;
        broadcastMessage("Welcome to the Trivia!");
        broadcastMessage("Questions will be given shortly. Please answer with True or False.");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }
        loop();
    }

    private void loop() {
        for (int round = 0; round < ROUNDS && isGameRunning; round++) {
            askQuestionToAllPlayers();
        }
        endGame();
    }


    private void endGame() {
        isGameRunning = false;
        Client winner = determineWinner();
        if (winner != null) {
            broadcastMessage("Game Over! The winner is: " + winner.getUsername() + " with a score of " + winner.getScore());
        } else {
            broadcastMessage("Game Over! No winner.");
        }
    }

    private Client determineWinner() {
        Client winner = null;
        int highestScore = -1;
        playerList_lock.lock();
        try {
            for (Client player : playerList) {
                if (player.getScore() > highestScore) {
                    highestScore = player.getScore();
                    winner = player;
                }
            }
        } finally {
            playerList_lock.unlock();
        }
        return winner;
    }

    private void broadcastMessage(String message) {
        playerList.forEach(player -> {
            try {
                Server.writeToClient(player.getSocket(), message);
            } catch (IOException e) {
                System.out.println("Error communicating with Client: " + e.getMessage());
            }
        });
    }

    private void askQuestionToAllPlayers() {
        TriviaResult question = triviaResponse.getRandomQuestion();
        broadcastMessage("Round Question: " + question.getQuestion() + " True or False?");

        CountDownLatch latch = new CountDownLatch(2);
        
        playerList.forEach(player -> 
            playerThreadPool.execute(() -> {
                handlePlayerAnswer(player, question.getCorrectAnswer(), latch);
            })
        );

        try {
            latch.await();  // Wait for both players to answer
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }
    }

    private void handlePlayerAnswer(Client player, String correctAnswer, CountDownLatch latch) {
        try {
            Server.writeToClient(player.getSocket(), Communication.PROVIDE_ANSWER   );
            String answer = Server.readFromClient(player.getSocket());
            if (answer.equalsIgnoreCase(correctAnswer)) {
                player.incrementScore();
                Server.writeToClient(player.getSocket(), "Correct! Your score: " + player.getScore());
            } else {
                Server.writeToClient(player.getSocket(), "Incorrect! Correct answer was: " + correctAnswer);
            }
        } catch (IOException e) {
            System.out.println("Error communicating with Client: " + e.getMessage());
        } finally {
            latch.countDown();
        }
    }
}
