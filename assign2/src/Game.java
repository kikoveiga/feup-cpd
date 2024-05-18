import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import game_logic.TriviaResponse;
import game_logic.TriviaResult;


public class Game {
    private List<Client> playerList;
    private TriviaResponse triviaResponse;
    private boolean isGameRunning;
    private final int ROUNDS = 4;

    public Game(List<Client> playerList) {
        this.playerList = playerList;
        this.triviaResponse = triviaResponse;
        this.isGameRunning = false;
    }

    // method to load questions into data set
    public void loadQuestions(String dataPath){
        File jsonFile = new File(dataPath);
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            // Read JSON file and map to TriviaResponse
            triviaResponse = objectMapper.readValue(jsonFile, new TypeReference<TriviaResponse>() {});

            // Output to verify
            //triviaResponse.getResults().forEach(q -> System.out.println(q.getQuestion() + " - " + q.getCorrectAnswer()));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startGame(){
        System.out.println("111");
        loadQuestions("src/database/questions.json");
        System.out.println("222");
        isGameRunning = true;
        broadcastMessage("Welcome to the Trivia!");
        System.out.println("333");
        broadcastMessage("Questions will be given shortly please answer with True or False");
        // pause time here
        try {
            Thread.sleep(5000); // 5 seconds
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        loop();
    }

    public void loop() {
        for (int round = 0; round <= ROUNDS; round++) {
            if (!isGameRunning) break;
            askQuestionToAllPlayers();
        }
        endGame();
    }

    public void endGame() {
        isGameRunning = false;
        //Client winner = determineWinner();
        //broadcastMessage("Game Over! The winner is: " + winner.getUsername() + " with a score of " + winner.getScore());
        broadcastMessage("heheh");
    }

    private Client determineWinner() {
        Client winner = null;
        int highestScore = -1;
        for (Client player : playerList) {
            if (player.getScore() > highestScore) {
                highestScore = player.getScore();
                winner = player;
            }
        }
        return winner;
    }

    // Broadcast a message to all players
    public void broadcastMessage(String message) {
        System.out.println(playerList);
        for (Client player : playerList) {
            System.out.println("444");
            try {
                Server.writeToClient(player.getSocket(), message);
            } catch (IOException e) {
                System.out.println("Error communicating with Client: " + e.getMessage());
            }
        }
    }

    private void askQuestionToAllPlayers() {
        TriviaResult question;
        broadcastMessage("Question: " + triviaResponse.getRandomQuestion().getQuestion());
        for (Client player : playerList) {
            handlePlayerAnswer(player);
        }
    }

    // Handles a player's answer to a question
    private void handlePlayerAnswer(Client player) {
        try {
            Server.writeToClient(player.getSocket(), Communication.PROVIDE_ANSWER);
            String answer = Server.readFromClient(player.getSocket());

            if (answer.equalsIgnoreCase(triviaResponse.getRandomQuestion().getCorrectAnswer())) {
                player.incrementScore();
                player.sendMessageToServer("Correct! Your score: " + player.getScore());
            }

        } catch (IOException e) {
            System.out.println("Error communicating with Client: " + e.getMessage());
        }
    }

    public static void main(String[] args){
        List<Client> players = new LinkedList<>();
        Game game = new Game(players);
        game.loadQuestions("src/database/questions.json");
        game.startGame();
    }
}