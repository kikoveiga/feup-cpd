import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {

    private String username;

    public Client(String username) {
        this.username = username;
    }

    private void writeToServer(Socket serverSocket, String message) {
        try {
            OutputStream output = serverSocket.getOutputStream();
            PrintWriter writer = new PrintWriter(output, true);
            writer.println(message);
        } catch (IOException exception) {
            System.out.println("Error writing to Server: " + exception.getMessage());
        }
    }

    public static void main(String[] args) {
        if (args.length < 2) return;

        String hostname = args[0];
        int port = Integer.parseInt(args[1]);

        Client client = new Client("player1");

        try (Socket socket = new Socket(hostname, port)) {

            String connectedMsg = String.format("Player %s connected", client.username);
            client.writeToServer(socket, connectedMsg);

            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));

            String time = reader.readLine();

            System.out.println(time);

        } catch (UnknownHostException ex) {
            System.out.println("Server not found: " + ex.getMessage());
        } catch (IOException ex) {
            System.out.println("I/O error: " + ex.getMessage());
        }
    }
}
