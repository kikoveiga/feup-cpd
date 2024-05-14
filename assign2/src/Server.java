import java.io.*;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private final ExecutorService executor;
    private final CustomThreadSafeList<Game> ongoingGames = new CustomThreadSafeList<>();
    private final int MAX_NUMBER_GAMES = 5;
    private final int MAX_PLAYERS_PER_GAME = 5;

    public Server() {
        executor = Executors.newFixedThreadPool(MAX_NUMBER_GAMES);
    }

    private void writeToClient(Socket clientSocket, String message) {
        try {
            OutputStream output = clientSocket.getOutputStream();
            PrintWriter writer = new PrintWriter(output, true);
            writer.println(message);
        } catch (IOException exception) {
            System.out.println("Error writing to Server: " + exception.getMessage());
        }
    }

    private void handleClient(Socket socket) throws IOException {
        InputStream input = socket.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        String message = reader.readLine();
        System.out.println("New client connected: " + message);

        String msgToClient = "You connected to the Game";
        writeToClient(socket, msgToClient);

        assignPlayerToGame(socket);

        monitorSocket(socket);
    }

    private void assignPlayerToGame(Socket socket) {
        // Create a New Game
        if (this.ongoingGames.size() == 0) {
            Game newGame = new Game();
            this.ongoingGames.add(newGame);
            newGame.addPlayer(socket);
        } else {
            Game game = this.searchForGame();
            if (game != null) {
                game.addPlayer(socket);
            } else {
                // TODO -> Handle what to do
                System.out.println("No available games");
            }
        }
    }

    // Logic to search for an available Game
    private Game searchForGame() {
        for (Game game : this.ongoingGames) {
            if (game.getPlayerCount() < MAX_PLAYERS_PER_GAME) {
                return game;
            }
        }

        // If there are no available Games
        return null;
    }

    private void removePlayerFromGame(Socket socket) {
        for (Game game : ongoingGames) {
            if (game.getPlayerSockets().contains(socket)) {
                System.out.println("A player disconnected.");
                game.removePlayer(socket);
            }
        }
    }
    
    // monitor the socket's input stream. 
    // When the socket is disconnected, it will catch an IOException.
    private void monitorSocket(Socket socket) {
        executor.execute(() -> {
            try {
                InputStream input = socket.getInputStream();
                while (input.read() != -1) {
                    // Keep reading to detect socket disconnection
                }
            } catch (IOException e) {
                // Socket is disconnected
                System.out.println("A player disconnected 2.");
                removePlayerFromGame(socket);
            }
        });
    }

    public static void main(String[] args) {
        if (args.length < 1) return;

        int port = Integer.parseInt(args[0]);

        try (ServerSocket serverSocket = new ServerSocket(port)) {

            System.out.println("Server is listening on port " + port);

            Server server = new Server();

            while (true) {
                Socket socket = serverSocket.accept();
                server.executor.execute(() -> {
                    try {
                        server.handleClient(socket);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            }

        } catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}