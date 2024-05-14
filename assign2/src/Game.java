import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

import game_logic.Deck;
import game_logic.Card;

public class Game {
    private List<Client> playerList;
    private Deck deck;
    private List<Card> dealerHand;

    public Game(List<Client> playerList) {
        this.playerList = playerList;
        this.deck = new Deck();
        this.dealerHand = new LinkedList<>();
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