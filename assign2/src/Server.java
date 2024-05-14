import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private final ExecutorService executor;
    private final CustomThreadSafeList<Socket> clientSockets = new CustomThreadSafeList<>();

    public Server() {
        int MAX_NUMBER_GAMES = 5;
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

        clientSockets.add(socket);
        if (clientSockets.size() >= 2) { // let's do 2 required players for now
            List<Socket> players = new ArrayList<>(clientSockets.subList(0, 2));
            clientSockets.removeAll(players);
            executor.execute(() -> startGame(players));
        }
    }

    private void startGame(List<Socket> players) {
        new Game(players.size(), players).start();
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