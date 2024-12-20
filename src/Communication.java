import java.util.Set;

public class Communication {

    // Server sends Client welcome packet
    public static final String WELCOME = "WELCOME";


    // Client informs Server he wants to authenticate (log in)
    public static final String CLIENT_AUTH = "CLIENT_AUTH";
    // Server asks Client for username
    public static final String AUTH_USERNAME = "AUTH_USERNAME";
    // Server asks Client for password
    public static final String AUTH_PASSWORD = "AUTH_PASSWORD";
    // Server confirms successful authentication
    public static final String AUTH_SUCCESS = "AUTH_SUCCESS";
    // Server informs about failed authentication
    public static final String AUTH_FAIL = "AUTH_FAIL";
    // Server informs that the User is already logged in
    public static final String AUTH_ALREADY_LOGGED_IN = "AUTH_ALREADY_LOGGED_IN";
    // Set with Authentication messages
    public static final Set<String> AUTH_MESSAGES = Set.of(AUTH_USERNAME, AUTH_PASSWORD, AUTH_SUCCESS, AUTH_FAIL, AUTH_ALREADY_LOGGED_IN);


    // Ping
    public static final String PING = "PING";
    // Pong (Answer to Ping)
    public static final String PONG = "PONG";


    // Send client its Session Token
    // Message content -> Session Token
    // Example -> "TOKEN mytoken123"
    public static final String TOKEN = "TOKEN";
    // Request session token
    public static final String REQUEST_TOKEN = "REQUEST_TOKEN";

    // Client informs Server he wants to reconnect
    public static final String CLIENT_RECONNECT = "CLIENT_RECONNECT";
    // Client informs server he disconnected
    public static final String CLIENT_DISCONNECT = "CLIENT_DISCONNECT";
    // Inform Client that he reconnected successfully 
    // Message content -> Reconnection queue position
    // Example -> "REQUEST_SUCCESS 2"
    public static final String RECONNECT_SUCCESS = "RECONNECT_SUCCESS";
    // Inform Client that reconnection was refused
    public static final String RECONNECT_FAIL = "RECONNECT_FAIL";
    // Inform Client that he can't reconnect because he is already logged in
    public static final String RECONNECT_ALREADY_LOGGED_IN = "RECONNECT_ALREADY_LOGGED_IN";


    // Client informs Server he wants to register
    public static final String CLIENT_REGISTER = "CLIENT_REGISTER";
    // Server asks Client for username
    public static final String REGISTER_USERNAME = "REGISTER_USERNAME";
    // Server asks Client for password
    public static final String REGISTER_PASSWORD = "REGISTER_PASSWORD";
    // Server informs Client that registration was a Success
    public static final String REGISTER_SUCCESS = "REGISTER_SUCCESS";
    // Server informs Client that registration Failed
    public static final String REGISTER_FAIL = "REGISTER_FAIL";
    // Set with Registration messages
    public static final Set<String> REGISTER_MESSAGES = Set.of(REGISTER_USERNAME, REGISTER_PASSWORD, REGISTER_SUCCESS, REGISTER_FAIL);


    // Request Client for question answer
    public static final String PROVIDE_ANSWER = "PROVIDE_ANSWER";
    // Client answers a question
    // Message content -> <ANSWER>
    // Example -> "ANSWER TRUE"
    public static final String ANSWER = "ANSWER";


    // Server asks Client to requeue or quit
    public static final String REQUEUE_OR_QUIT = "REQUEUE_OR_QUIT";
    // Client informs Server he wants to Requeue
    public static final String REQUEUE = "REQUEUE";
    // Client informs Server he wants to Quit
    public static final String QUIT = "QUIT";
}
