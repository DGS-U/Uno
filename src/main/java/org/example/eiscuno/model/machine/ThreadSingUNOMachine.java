package org.example.eiscuno.model.machine;

import javafx.fxml.FXML;
import org.example.eiscuno.model.card.Card;
import org.example.eiscuno.observer.EventManager;

import java.util.ArrayList;

public class ThreadSingUNOMachine implements Runnable {
    private ArrayList<Card> cardsPlayer;
    private ArrayList<Card> cardsMachine;
    private EventManager eventManager;
    private boolean sangUnoToPlayer = false;
    private boolean sangUnoToMachine = false;

    public ThreadSingUNOMachine(ArrayList<Card> cardsPlayer,
                                ArrayList<Card> cardsMachine,
                                EventManager eventManager){
        this.cardsPlayer = cardsPlayer;
        this.cardsMachine = cardsMachine;
        this.eventManager = eventManager;
    }

    @Override
    public void run(){
        while (true){
            try {
                Thread.sleep((long) (Math.random() * 5000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            hasOneCardTheHumanPlayer();
            hasOneCardTheMachinePlayer();
        }
    }

    private void hasOneCardTheHumanPlayer(){
        if (cardsPlayer.size() == 1 && !sangUnoToPlayer) {
            System.out.println("the machine sang uno to the player");
            sangUnoToPlayer = true;
            eventManager.notify("sangUnoToPlayer", sangUnoToPlayer);
        }
    }

    private void hasOneCardTheMachinePlayer(){
        if (cardsMachine.size() == 1 && !sangUnoToMachine) {
            System.out.println("the machine sang uno to itself");
            sangUnoToMachine = true;
            eventManager.notify("sangUnoToMachine", sangUnoToMachine);
        }
    }

    public void changeSangToMachine(boolean value){
        sangUnoToMachine = value;
    }

    public void changeSangToPlayer(boolean value){
        sangUnoToPlayer = value;
    }
}
