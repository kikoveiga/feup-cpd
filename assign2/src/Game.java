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
        isGameRunning = true;
        broadcastMessage("Welcome to the Trivia!");
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
        for (Client player : playerList) {
            player.sendMessageToServer(message);
        }
    }

    private void askQuestionToAllPlayers() {
        TriviaResult question;
        broadcastMessage("Question: " + triviaResponse.getRandomQuestion().getQuestion());
        for (Client player : playerList) {
            String answer = player.receiveMessage();
            if (answer.equalsIgnoreCase(triviaResponse.getRandomQuestion().getCorrectAnswer())) {
                player.incrementScore();
                player.sendMessageToServer("Correct! Your score: " + player.getScore());
                break;
            }
        }
    }

    public static void main(String[] args){
        List<Client> players = new LinkedList<>();
        Game game = new Game(players);
        game.loadQuestions("/home/belchior/Desktop/g17/assign2/src/database/questions.json");
        game.startGame();
    }


}