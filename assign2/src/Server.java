import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Server {

    private final ExecutorService executor;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    
    // General Info
    private final int MAX_NUMBER_GAMES = 5;
    private final int PLAYERS_PER_GAME = 3;

    // Client Queue
    private final List<Client> clientQueue;
    private final Lock clientQueue_lock = new ReentrantLock();

    // Game List
    private final List<Game> gameList;
    private final Lock gameList_lock = new ReentrantLock();

    // Database
    private final UserDatabase userDatabase;
    private final Lock userDatabase_lock = new ReentrantLock();

    public Server() throws IOException{
        this.clientQueue = new ArrayList<Client>();
        this.gameList = new ArrayList<Game>();
        this.userDatabase = new UserDatabase();
        executor = Executors.newFixedThreadPool(MAX_NUMBER_GAMES);
        schedulePing();
    }

    private void writeToClient(Socket clientSocket, String message) throws IOException{
        OutputStream output = clientSocket.getOutputStream();
        PrintWriter writer = new PrintWriter(output, true);
        writer.println(message);
    }

    private String readFromClient(Socket clientSocket) throws IOException {
        InputStream input = clientSocket.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        return reader.readLine();
    }

    private void handleClient(Socket socket) throws IOException {
        Client client = new Client(socket);
        System.out.println("[AUTH] A Client is authenticating");
        if (authenticateClient(client)) {
            System.out.println("[AUTH] " + client.getUsername() + " authenticated successfully");
            writeToClient(client.getSocket(), Communication.AUTH_SUCCESS);
            addClientToQueue(client);
            checkForNewGame();
        } else {
            System.out.println("[AUTH] " + client.getUsername() + " failed authentication");
            writeToClient(socket, Communication.AUTH_FAIL);
            socket.close();
        }

        pingAllClients();
    }

    private boolean authenticateClient(Client client) throws IOException{
        writeToClient(client.getSocket(), Communication.AUTH);
        String username = readFromClient(client.getSocket());
        client.setUsername(username);

        writeToClient(client.getSocket(), Communication.PASS);
        String password = readFromClient(client.getSocket());

        userDatabase_lock.lock();
        boolean authSuccess = userDatabase.authenticate(username, password) == true;
        userDatabase_lock.unlock();

        return authSuccess;
    }

    // Adds a Client to the clientQueue
    private void addClientToQueue(Client client) {
        clientQueue_lock.lock();
        clientQueue.add(client);
        String log = String.format("[QUEUE] Client %s was added to the Queue (%d/%d)", client.getUsername(), clientQueue.size(), PLAYERS_PER_GAME);
        System.out.println(log);
        clientQueue_lock.unlock();
    }

    // Checks if a new Game should start
    private void checkForNewGame() {
        clientQueue_lock.lock();
        if (clientQueue.size() == PLAYERS_PER_GAME) {
            startNewGame(clientQueue);
        }
        clientQueue_lock.unlock();
    }

    // Starts a new game with players (Clients) in playerList
    private void startNewGame(List<Client> playerList) {
        Game game = new Game(playerList);
        gameList_lock.lock();
        gameList.add(game);
        String log = String.format("[Game %d] Started Game", gameList.size());
        System.out.println(log);
        gameList_lock.unlock();
        clientQueue.clear();
    }

    // Pings client
    private void pingClient(Client client) throws IOException{
        writeToClient(client.getSocket(), Communication.PING);
    }

    // Ping all clients in Queue
    private void pingAllClients() throws IOException{
        clientQueue_lock.lock();
        for (Client client : clientQueue) {
            pingClient(client);
        }
        clientQueue_lock.unlock();
    } 

    private void schedulePing() throws IOException {
        scheduler.scheduleAtFixedRate(() -> {
            try {
                pingAllClients();
            } catch (IOException e) {
                System.out.println("Failed to ping clients: " + e.getMessage());
            }
        }, 0, 5, TimeUnit.SECONDS);
    }

    public static void main(String[] args) {
        if (args.length < 1) return;

        int port = Integer.parseInt(args[0]);

        try (ServerSocket serverSocket = new ServerSocket(port)) {

            System.out.println("Server is listening on port " + port);

            Server server = new Server();
            // server.pingClients();

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