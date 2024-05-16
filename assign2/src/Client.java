import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {

    private String username;
    private int rank;
    private Socket socket;
    private long lastResponseTime;
    private BufferedReader consoleReader;
    private BufferedReader serverReader;
    private PrintWriter serverWriter;
    private String sessionToken;


    public Client(Socket socket) throws IOException {
        this.socket = socket;
        this.consoleReader = new BufferedReader(new InputStreamReader(System.in));
        this.serverReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.serverWriter = new PrintWriter(socket.getOutputStream(), true);
    }

    public String getUsername() {
        return this.username;
    }

    public Socket getSocket() {
        return this.socket;
    }

    public int getRank() {
        return this.rank;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }

    public void clearSessionToken() {
        this.sessionToken = "";
    }

    public long getLastResponseTime() {
        return lastResponseTime;
    }

    public void setLastResponseTime() {
        this.lastResponseTime = System.currentTimeMillis();
    }

    private void sendMessageToServer(String message) {
        serverWriter.println(message);
    }

    private void handleAuthentication(String serverMessage) throws IOException {
        switch (serverMessage) {
            case Communication.AUTH:
                System.out.print("Username: ");
                String username = consoleReader.readLine();
                setUsername(username);
                sendMessageToServer(username);
                break;
            case Communication.PASS:
                System.out.print("Password: ");
                String password = consoleReader.readLine();
                sendMessageToServer(password);
                break;
            case Communication.AUTH_FAIL:
                System.out.println("Authentication failed. Disconnecting...");
                socket.close();
                break;
            case Communication.AUTH_SUCCESS:
                System.out.println("Authenticated successfully.");
                break;
        }
    }

    private void handleServerMessage(String serverMessage) throws IOException {
        if (serverMessage.equals(Communication.PING)) {
            sendMessageToServer(Communication.PONG);
        } else if (Communication.AUTH_MESSAGES.contains(serverMessage)) {
            handleAuthentication(serverMessage);
        } else if (serverMessage.startsWith(Communication.TOKEN)) {
            storeToken(getMessageContent(serverMessage));
        }
        else {
            System.out.println(serverMessage);
        }
    }

    private void readServerMessages() throws IOException {
        String serverMessage;
        while ((serverMessage = serverReader.readLine()) != null) {
            handleServerMessage(serverMessage);
        }
    }

    // Example : "PROTOCOL CONTENT"
    // retrieves CONTENT
    private String getMessageContent(String serverMessage) {
        String[] parts = serverMessage.split(" ");
        if (parts.length >= 2) {
            return parts[1];
        } else {
            return "";
        }
    }

    // Stores the token in a file 
    // This simulates what would be the Client's system storage
    private void storeToken(String sessionToken) {
        try {
            String filename = "token-" + this.username + ".txt";
            File file = new File("src/database/tokens/" + filename);

            // Create the file if it doesn't exist
            FileWriter writer = new FileWriter(file, false);
            writer.write(sessionToken);
            writer.close();
            System.out.println("Token stored successfully.");
        } catch (IOException e) {
            System.out.println("Error storing token: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: java Client <hostname> <port>");
            return;
        }

        String hostname = args[0];
        int port = Integer.parseInt(args[1]);

        try (Socket socket = new Socket(hostname, port)) {
            Client client = new Client(socket);
            client.readServerMessages();
        } catch (UnknownHostException ex) {
            System.out.println("Server not found: " + ex.getMessage());
        } catch (IOException ex) {
            System.out.println("I/O error: " + ex.getMessage());
        }
    }
}
