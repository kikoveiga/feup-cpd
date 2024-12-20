import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

public class UserDatabase {
    private static final String FILE_PATH = "src/database/users.json";
    private Map<String, User> users;
    private final HashSet<String> loggedInUsers = new HashSet<>();
    private final ObjectMapper objectMapper;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final BCryptPasswordEncoder tokenEncoder = new BCryptPasswordEncoder();

    public UserDatabase() throws IOException {
        this.objectMapper = new ObjectMapper();
        loadUsers();
    }

    // Adds user to loggedInUsers
    void userLoggedIn(String username) { loggedInUsers.add(username); }

    // Removes user from loggedInUsers
    void userLoggedOut(String username) { loggedInUsers.remove(username); }

    // Checks if user is logged in
    boolean isUserLoggedIn(String username) { return loggedInUsers.contains(username); }

    // Loads users from database file
    private void loadUsers() throws IOException {
        File file = new File(FILE_PATH);
        if (file.exists()) {
            if (file.length() == 0) {
                users = new HashMap<>();
                return;
            }
            users = objectMapper.readValue(file, new TypeReference<>() {
            });
        } else {
            throw new IOException("User database file not found.");
        }
    }

    // Saves users to database file
    private void saveUsers() throws IOException {
        objectMapper.writeValue(new File(FILE_PATH), users);
    }

    // Verifies if user with username:password exists in the database file
    public boolean authenticate(String username, String password) {
        User user = users.get(username);
        return user != null && passwordEncoder.matches(password, user.getPassword());
    }

    // Increments User rank by 'addedRank'
    public void incrementRank(String username, int addedRank) throws IOException{
        User user = users.get(username);
        if (user != null) {
            int currRank = user.getRank();
            user.setRank(currRank + addedRank);
            saveUsers();
        }
    }

    // Gets rank from user with 'username'
    public int getUserRank(String username) {
        User user = users.get(username);
        return user != null ? user.getRank() : -1; // Return -1 if user is not found
    }

    // Generates a unique session token
    public String generateSessionToken() {
        return UUID.randomUUID().toString();
    }

    // Assigns session token
    // Returns not encoded session token
    // Or null if user doesn't exist
    public String assignSessionToken(String username) throws IOException {
        User user = users.get(username);
        if (user != null) {
            String token = generateSessionToken();
            String encodedToken = tokenEncoder.encode(token);
            user.setSessionToken(encodedToken);
            saveUsers();
            return token;
        }

        return null;
    }

    // Gets username form a sessionToken
    // returns null if no user found with that sessionToken
    public String getUsernameFromToken(String sessionToken) {
        for (Map.Entry<String, User> entry : users.entrySet()) {
            String retrievedToken = entry.getValue().getSessionToken();
            if (retrievedToken != null && tokenEncoder.matches(sessionToken, retrievedToken)) {
                return entry.getKey();
            }
        }
        return null;
    }

    // Creates a new user and adds it to the database
    public void createUser(String username, String password) throws IOException {

        if (!users.containsKey(username)) {
            String encodedPassword = passwordEncoder.encode(password);
            User newUser = new User(encodedPassword, 100);
            users.put(username, newUser);
            saveUsers();
        } else {
            throw new IllegalArgumentException("Username already exists.");
        }
    }

    public static class User {
        private String password;
        private int rank;
        private String sessionToken;

        public User() {
        }

        public User(String password, int rank) {
            this.password = password;
            this.rank = rank;
        }

        public String getPassword() {
            return password;
        }

        public int getRank() {
            return rank;
        }

        public void setRank(Integer rank) {
            this.rank = rank;
        }

        public String getSessionToken() {
            return sessionToken;
        }
    
        public void setSessionToken(String sessionToken) {
            this.sessionToken = sessionToken;
        }

    }

    // Main method only for testing purposes
    public static void main(String[] args) {
        try {
            UserDatabase userDatabase = new UserDatabase();
            
            // Test authentication
            System.out.println("Authenticating user1 with correct password: " + userDatabase.authenticate("user1", "password1")); // true
            System.out.println("Authenticating user2 with wrong password: " + userDatabase.authenticate("user2", "wrongpassword")); // false

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
