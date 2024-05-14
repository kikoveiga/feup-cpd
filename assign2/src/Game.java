import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

import game_logic.Deck;
import game_logic.Card;

public class Game {
    private CustomThreadSafeList<Socket> playerSockets;
    private Deck deck;
    private List<Card> dealerHand;

    private final int MAX_PLAYERS_PER_GAME = 5;
    private final int MIN_PLAYERS_PER_GAME = 2;

    public Game() {
        this.playerSockets = new CustomThreadSafeList<Socket>();
        this.deck = new Deck();
        this.dealerHand = new LinkedList<>();
        this.updateState();
    }

    public int getPlayerCount() {
        return this.playerSockets.size();
    }

    public void addPlayer(Socket playerSocket) {
        this.playerSockets.add(playerSocket);
        this.updateState();
    }

    public void updateState() {
        if (this.getPlayerCount() < MIN_PLAYERS_PER_GAME) {
            this.waitForPlayers();
        } else if (this.getPlayerCount() >= MIN_PLAYERS_PER_GAME) {
            this.start();
        }
    }

    public void waitForPlayers() {
        // Not sure if message printed here or in Server
        if (this.getPlayerCount() > 0) {
            System.out.println("Waiting in Lobby, " + this.getPlayerCount() + " players connected");
        }
    }

    public void start() {
        System.out.println("Starting blackjack game with " + playerSockets.size() + " players");
    
        // Deal initial two cards to each player and the dealer
        // for (Client client : playerSockets) {
        //     dealInitialCards(client);
        // }

        dealInitialCardsToDealer();
    }

    private void dealInitialCards(Client client) {
        Card firstCard = deck.drawCard();
        Card secondCard = deck.drawCard();

        // TODO -> give cards to client
    }

    private void dealInitialCardsToDealer() {
        Card firstCard = deck.drawCard();
        Card secondCard = deck.drawCard();

        this.dealerHand.add(firstCard);
        this.dealerHand.add(secondCard);

        // TODO -> handle this at the server
    }
}