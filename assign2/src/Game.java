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


public class Game {
    private List<Client> playerList;
    private TriviaResponse triviaResponse;
    public Game(List<Client> playerList) {
        this.playerList = playerList;
    }

    // method to load questions into data set
    public void loadQuestions(String dataPath){
        File jsonFile = new File(dataPath);
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            // Read JSON file and map to TriviaResponse
            TriviaResponse triviaResponse = objectMapper.readValue(jsonFile, new TypeReference<TriviaResponse>() {});

            // Convert List of TriviaResult to List of TriviaQuestion
            //questions = triviaResponse.getResults().stream()
                    //.map(result -> new TriviaQuestion(result.getQuestion(), result.getCorrectAnswer()))
                    //.collect(Collectors.toList());

            // Output to verify
            triviaResponse.getResults().forEach(q -> System.out.println(q.getQuestion() + " - " + q.getCorrectAnswer()));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void startGame(){

        //test if questions work

    }

    // Broadcast a message to all players
    public void broadcastMessage(String message) {
        for (Client player : playerList) {
            player.(message);
        }
    }

    public static void main(String[] args){
        List<Client> players = new LinkedList<>();
        Game game = new Game(players);
        game.loadQuestions("/home/belchior/Desktop/g17/assign2/src/database/questions.json");
        game.startGame();
    }


}