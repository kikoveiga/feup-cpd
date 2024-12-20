package game_logic;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Random;

public class TriviaResponse {
    private int responseCode;
    private List<TriviaResult> results;

    @JsonProperty("response_code")
    public int getResponseCode() {
        return responseCode;
    }

    @JsonProperty("response_code")
    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public List<TriviaResult> getResults() {
        return results;
    }

    public void setResults(List<TriviaResult> results) {
        this.results = results;
    }

    public TriviaResult getRandomQuestion() {
        Random random = new Random();
        int randomIndex = random.nextInt(results.size());
        return results.get(randomIndex);
    }
}

