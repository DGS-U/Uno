package org.example.eiscuno.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import org.example.eiscuno.model.card.Card;
import org.example.eiscuno.model.deck.Deck;
import org.example.eiscuno.model.game.GameUno;
import org.example.eiscuno.model.machine.ThreadPlayMachine;
import org.example.eiscuno.model.machine.ThreadSingUNOMachine;
import org.example.eiscuno.model.player.Player;
import org.example.eiscuno.model.table.Table;
import org.example.eiscuno.model.unoenum.EISCUnoEnum;
import org.example.eiscuno.observer.EventListener;
import org.example.eiscuno.observer.EventManager;

import java.util.Objects;

/**
 * Controller class for the Uno game.
 */
public class GameUnoController implements EventListener {

    @FXML
    private GridPane gridPaneCardsMachine;

    @FXML
    private GridPane gridPaneCardsPlayer;

    @FXML
    private ImageView tableImageView;

    private Player humanPlayer;
    private Player machinePlayer;
    private Deck deck;
    private Table table;
    private GameUno gameUno;
    private int posInitCardToShow;

    private ThreadSingUNOMachine threadSingUNOMachine;
    private ThreadPlayMachine threadPlayMachine;
    private boolean isPlayerTurn;
    private boolean sangUnoToPlayer;
    private boolean sangUnoToMachine;
    private EventManager eventManager;

    /**
     * Initializes the controller.
     */
    @FXML
    public void initialize() {
        initVariables();
        this.gameUno.startGame();
        printCardsHumanPlayer();
        printCardsMachinePlayer();
        eventManager = new EventManager();

        threadSingUNOMachine =
                new ThreadSingUNOMachine(this.humanPlayer.getCardsPlayer(),
                        this.machinePlayer.getCardsPlayer(),
                        this.eventManager);
        Thread t = new Thread(threadSingUNOMachine, "ThreadSingUNO");
        t.start();

        isPlayerTurn = true;
        sangUnoToPlayer = false;
        sangUnoToMachine = false;
        threadPlayMachine = new ThreadPlayMachine(this.table, this.deck,
                this.machinePlayer, this.tableImageView, this.eventManager,
                this.threadSingUNOMachine);
        threadPlayMachine.start();
        eventManager.subscribe("isPlayerTurn", this);
        eventManager.subscribe("sangUnoToPlayer", this);
        eventManager.subscribe("sangUnoToMachine", this);
        Card firstCard = deck.takeCard();
        table.addCardOnTheTable(firstCard);
        tableImageView.setImage(firstCard.getImage());
    }

    /**
     * Initializes the variables for the game.
     */
    private void initVariables() {
        this.humanPlayer = new Player("HUMAN_PLAYER");
        this.machinePlayer = new Player("MACHINE_PLAYER");
        this.deck = new Deck();
        this.table = new Table();
        this.gameUno = new GameUno(this.humanPlayer, this.machinePlayer, this.deck, this.table);
        this.posInitCardToShow = 0;
    }

    private void printCardsMachinePlayer() {
        Platform.runLater(() -> {
            this.gridPaneCardsMachine.getChildren().clear();
            Card[] currentVisibleCardsMachinePlayer = this.gameUno.getCurrentVisibleCardsMachinePlayer(0);
            for (int i = 0; i < currentVisibleCardsMachinePlayer.length; i++) {
                ImageView cardImageView = new ImageView(EISCUnoEnum.CARD_UNO.getFilePath());
                cardImageView.setY(16);
                cardImageView.setFitHeight(90);
                cardImageView.setFitWidth(70);
                this.gridPaneCardsMachine.add(cardImageView, i, 0);
            }
        });
    }

    /**
     * Prints the human player's cards on the grid pane.
     */
    private void printCardsHumanPlayer() {
        Platform.runLater(() -> {
            this.gridPaneCardsPlayer.getChildren().clear();
            Card[] currentVisibleCardsHumanPlayer = this.gameUno.getCurrentVisibleCardsHumanPlayer(this.posInitCardToShow);

            for (int i = 0; i < currentVisibleCardsHumanPlayer.length; i++) {
                Card card = currentVisibleCardsHumanPlayer[i];
                ImageView cardImageView = card.getCard();

                cardImageView.setOnMouseClicked((MouseEvent event) -> {
                    System.out.println(isPlayerTurn);
                    if (!isPlayerTurn || !gameUno.canPlayCard(card)) {
                        return;
                    }

                    gameUno.playCard(card);
                    tableImageView.setImage(card.getImage());
                    humanPlayer.removeCard(findPosCardsHumanPlayer(card));
                    threadPlayMachine.setIsPlayerTurn(false);
                    isPlayerTurn = false;
                    printCardsHumanPlayer();
                });

                this.gridPaneCardsPlayer.add(cardImageView, i, 0);
            }
        });
    }

    /**
     * Finds the position of a specific card in the human player's hand.
     *
     * @param card the card to find
     * @return the position of the card, or -1 if not found
     */
    private Integer findPosCardsHumanPlayer(Card card) {
        for (int i = 0; i < this.humanPlayer.getCardsPlayer().size(); i++) {
            if (this.humanPlayer.getCardsPlayer().get(i).equals(card)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Handles the "Back" button action to show the previous set of cards.
     *
     * @param event the action event
     */
    @FXML
    void onHandleBack(ActionEvent event) {
        if (this.posInitCardToShow > 0) {
            this.posInitCardToShow--;
            printCardsHumanPlayer();
        }
    }

    /**
     * Handles the "Next" button action to show the next set of cards.
     *
     * @param event the action event
     */
    @FXML
    void onHandleNext(ActionEvent event) {
        if (this.posInitCardToShow < this.humanPlayer.getCardsPlayer().size() - 4) {
            this.posInitCardToShow++;
            printCardsHumanPlayer();
        }
    }

    /**
     * Handles the action of taking a card.
     *
     * @param event the action event
     */
    @FXML
    void onHandleTakeCard(ActionEvent event) {
        Card card = this.deck.takeCard();
        this.humanPlayer.addCard(card);
        printCardsHumanPlayer();
        threadPlayMachine.setIsPlayerTurn(false);
        isPlayerTurn = false;
        if (sangUnoToPlayer) {
            sangUnoToPlayer = false;
        }
    }

    /**
     * Handles the action of saying "Uno".
     *
     * @param event the action event
     */
    @FXML
    void onHandleUno(ActionEvent event) {
        // Implement logic to handle Uno event here
        if (humanPlayer.getCardsPlayer().size() == 1 && !sangUnoToPlayer) {
            sangUnoToPlayer = true;
            threadSingUNOMachine.changeSangToPlayer(sangUnoToPlayer);
            return;
        }
        if (machinePlayer.getCardsPlayer().size() == 1 && !sangUnoToMachine) {
            threadSingUNOMachine.changeSangToMachine(true);
            gameUno.eatCard(machinePlayer, 2);
            printCardsMachinePlayer();
            threadSingUNOMachine.changeSangToMachine(false);
        }
    }

    @Override
    public void update(String key, boolean message) {
        if (Objects.equals(key, "isPlayerTurn")) {
            isPlayerTurn = message;
            if (isPlayerTurn) {
                printCardsMachinePlayer();
            }
            return;

        }
        if (Objects.equals(key, "sangUnoToPlayer")) {
            sangUnoToPlayer = message;
            if (sangUnoToPlayer) {
                gameUno.eatCard(humanPlayer, 2);
                printCardsHumanPlayer();
            }
            return;
        }
        if (Objects.equals(key, "sangUnoToMachine")) {
            sangUnoToMachine = message;
        }
    }
}
