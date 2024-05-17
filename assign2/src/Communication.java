import java.util.Set;

public class Communication {
    // Server sends Client welcome packet
    public static final String WELCOME = "WELCOME";
    // Client informs Server he wants to authenticate (log in)
    public static final String CLIENT_AUTH = "CLIENT_AUTH";
    // Client informs Server he wants to reconnect
    public static final String CLIENT_RECONNECT = "CLIENT_RECONNECT";
    // Server asks Client for username
    public static final String AUTH = "AUTH";
    // Server asks Client for password
    public static final String PASS = "PASS";
    // Server confirms successful authentication
    public static final String AUTH_SUCCESS = "AUTH_SUCCESS";
    // Server informs about failed authentication
    public static final String AUTH_FAIL = "AUTH_FAIL";
    // Hash Set with Authentication messages
    public static final Set<String> AUTH_MESSAGES = Set.of(AUTH, PASS, AUTH_SUCCESS, AUTH_FAIL);
    // Ping
    public static final String PING = "PING";
    // Pong (Answer to Ping)
    public static final String PONG = "PONG";
    // Send client its Session Token
    public static final String TOKEN = "TOKEN";
    // Request session token
    public static final String REQUEST_TOKEN = "REQUEST_TOKEN";
    // Inform Client that he reconnected successfully 
    public static final String RECONNECT_SUCCESS = "REQUEST_SUCCESS";
    // Inform Client that reconnection was refused
    public static final String RECONNECT_FAIL = "REQUEST_FAIL";
}
