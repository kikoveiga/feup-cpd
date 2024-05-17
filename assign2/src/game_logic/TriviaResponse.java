package game_logic;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

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
}

