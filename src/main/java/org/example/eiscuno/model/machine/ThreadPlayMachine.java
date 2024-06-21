package org.example.eiscuno.model.machine;

import javafx.scene.image.ImageView;
import org.example.eiscuno.model.card.Card;
import org.example.eiscuno.model.deck.Deck;
import org.example.eiscuno.model.player.Player;
import org.example.eiscuno.model.table.Table;

import java.util.Optional;

public class ThreadPlayMachine extends Thread {
    private Table table;
    private Deck deck;
    private Player machinePlayer;
    private ImageView tableImageView;
    private volatile boolean hasPlayerPlayed;

    public ThreadPlayMachine(Table table, Deck deck, Player machinePlayer, ImageView tableImageView) {
        this.table = table;
        this.deck = deck;
        this.machinePlayer = machinePlayer;
        this.tableImageView = tableImageView;
        this.hasPlayerPlayed = false;
    }

    public void run() {
        while (true) {
            if (hasPlayerPlayed) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                putCardOnTheTable();
                hasPlayerPlayed = false;
            }
        }
    }

    private void putCardOnTheTable() {
        Card currentCard = table.getCurrentCardOnTheTable();
        Optional<Card> validCard = machinePlayer.getCardsPlayer().stream().filter(
                card -> card.getColor().equals(currentCard.getColor()) || card.getValue().equals(currentCard.getValue())
        ).findFirst();
        if (validCard.isEmpty()) {
            machinePlayer.addCard(deck.takeCard());
            return;
        }

        table.addCardOnTheTable(validCard.get());
        tableImageView.setImage(validCard.get().getImage());
        machinePlayer.removeCard(machinePlayer.getCardsPlayer().indexOf(validCard.get()));
    }

    public void setHasPlayerPlayed(boolean hasPlayerPlayed) {
        this.hasPlayerPlayed = hasPlayerPlayed;
    }
}
