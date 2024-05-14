import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private final ExecutorService executor;
    private final CustomThreadSafeList<Game> ongoingGames = new CustomThreadSafeList<>();
    private final int MAX_NUMBER_GAMES = 5;

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
    }

    private void assignPlayerToGame(Socket socket) {
        if (this.ongoingGames.size() == 0) {
            Game newGame = new Game();
            newGame.addPlayer(socket);
        }
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