# Trivia Quiz Game Server

## Running the Project

Ensure you have **Java JDK 21 or higher** installed on your system to compile and run this project.

### Compilation

First, compile the source code. Navigate to the **assign2 directory** of the project where the src folder and the lib directory are located. Use the following command to compile all Java files, storing the compiled classes in the out directory:

```
javac -cp "lib/*" -d out src/*.java src/game_logic/*.java
```

This command includes all libraries located in the lib directory in the classpath and compiles all Java files in the src and src/game_logic directories.


### Running the Server

Start the server by specifying a port number on which the server will listen for incoming client connections. Ensure that the out directory is included in the classpath along with the libraries:

```
java -cp "lib/*:out" Server <port>
```

Replace port with the port number you wish to use (e.g., 12345).

### Connecting Clients

And connect **Clients**:

After the server is running, you can connect clients to it. Run the following command from a different terminal window for each client:


```
java -cp "lib/*:out" Client localhost <port>
```

Again, replace port with the same port number used to start the server.
