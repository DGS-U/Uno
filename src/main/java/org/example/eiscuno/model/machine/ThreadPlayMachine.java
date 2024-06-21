package org.example.eiscuno.model.machine;

import javafx.scene.image.ImageView;
import org.example.eiscuno.model.card.Card;
import org.example.eiscuno.model.deck.Deck;
import org.example.eiscuno.model.player.Player;
import org.example.eiscuno.model.table.Table;
import org.example.eiscuno.observer.TurnEventManager;

import java.util.Optional;

public class ThreadPlayMachine extends Thread {
    private Table table;
    private Deck deck;
    private Player machinePlayer;
    private ImageView tableImageView;
    private TurnEventManager turnEventManager;
    private volatile boolean isPlayerTurn;

    public ThreadPlayMachine(Table table, Deck deck, Player machinePlayer, ImageView tableImageView, TurnEventManager turnEventManager) {
        this.table = table;
        this.deck = deck;
        this.machinePlayer = machinePlayer;
        this.tableImageView = tableImageView;
        this.isPlayerTurn = true;
        this.turnEventManager = turnEventManager;
    }

    public void run() {
        while (true) {
            if (!isPlayerTurn) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                putCardOnTheTable();
                turnEventManager.notify(true);
                isPlayerTurn = true;
            }
        }
    }

    private void putCardOnTheTable() {
        Card currentCard = table.getCurrentCardOnTheTable();
        Optional<Card> validCard = machinePlayer.getCardsPlayer().stream().filter(
                card -> card.getColor().equals(currentCard.getColor()) || card.getValue().equals(currentCard.getValue())
        ).findFirst();
        if (validCard.isEmpty()) {
            System.out.println("Machine player turn - No valid card to play. Taking a card from the deck.");
            machinePlayer.addCard(deck.takeCard());
            return;
        }

        System.out.println("Machine player turn - Playing card: " + validCard.get());
        table.addCardOnTheTable(validCard.get());
        tableImageView.setImage(validCard.get().getImage());
        machinePlayer.removeCard(machinePlayer.getCardsPlayer().indexOf(validCard.get()));
    }

    public void setIsPlayerTurn(boolean isPlayerTurn) {
        this.isPlayerTurn = isPlayerTurn;
    }
}
