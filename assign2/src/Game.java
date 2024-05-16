import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import game_logic.TriviaQuestion;

public class Game {
    private List<Client> playerList;
    private List<TriviaQuestion> questions;

    public Game(List<Client> playerList) {
        this.playerList = playerList;
    }
    // method to load questions into data set
    public void loadQuestions(String dataPath){
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String jsonData = new String(Files.readAllBytes(Paths.get(dataPath)));
            JsonNode rootNode = objectMapper.readTree(jsonData);
            JsonNode resultsNode = rootNode.path("results");

            for (JsonNode questionNode : resultsNode) {
                String question = questionNode.path("question").asText();
                String correctAnswer = questionNode.path("correct_answer").asText();
                TriviaQuestion triviaQuestion = new TriviaQuestion(question, correctAnswer);
                questions.add(triviaQuestion);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void startGame(){

        //test if questions work
        for (TriviaQuestion q : questions){
            System.out.println("question");
            System.out.println(q.getQuestion());
            System.out.println("answers");
            System.out.println(q.getCorrectAnswer());
        }
    }

    // Broadcast a message to all players
    public void broadcastMessage(String message) {
        for (Client player : playerList) {
            //player.sendMessage(message);
        }
    }

    public static void main(String[] args){
        List<Client> players = new LinkedList<>();
        Game game = new Game(players);
        game.loadQuestions("/home/belchior/Desktop/g17/assign2/src/database/questions.json");
        game.startGame();
    }


}