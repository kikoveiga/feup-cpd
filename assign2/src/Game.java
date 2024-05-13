import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

import game_logic.Deck;
import game_logic.Card;

public class Game {
    private List<Client> playersList;
    private Deck deck;
    private List<Card> dealerHand;

    public Game(int players, List<Client> playerSockets) {
        this.playersList = playersList;
        this.deck = new Deck();
        this.dealerHand = new LinkedList<>();
    }
    public void start() {
        System.out.println("Starting blackjack game with " + playersList.size() + " players");
    
        // Deal initial two cards to each player and the dealer
        for (Client client : playersList) {
            dealInitialCards(client);
        }

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