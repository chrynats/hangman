/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.csdfossteam.hangman.face.gui;


import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Hashtable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.csdfossteam.hangman.core.HangMan;
import com.csdfossteam.hangman.core.Life;
import com.csdfossteam.hangman.core.WordDictionary;
import com.csdfossteam.hangman.core.inputString;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * <h1>Implements a Hangman GUI Class</h1>
 *  *
 * <p>
 * <b>Note:</b> Incomplete
 *
 * @author  nasioutz
 * @version 0.5
 * @since   2018-17-12
 */
public class HangmanGUI extends Application implements EventHandler<ActionEvent>
{

    /*-----------------------------------
    MULTITHREAD RELATED PART OF THE CLASS
    ------------------------------------*/


    private static final CountDownLatch latch = new CountDownLatch(1);
    private static HangmanGUI gui = null;


    public static HangmanGUI getGUIinstance() {
         try
        {
            latch.await();
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        return gui;
    }

    public static void setGUI(HangmanGUI gui0) {
        gui = gui0;
        latch.countDown();
    }


    /*----------------------------------
            --- CONSTRUCTOR ---
    ------------------------------------*/

    public HangmanGUI() throws IOException {
        setGUI(this);
    }


    /*----------------------------------
    --- NON STATIC PART OF THE CLASS ---
    ------------------------------------*/

    public TextField input;
    public Label text;
    public ImageView hangman_img;

    public Stage gameStage; //Public to be accessible from outside it's thread

    private String dirPath = new java.io.File( "." ).getCanonicalPath();
    private String dirPathToData = Paths.get(dirPath,"data").toString();
    private inputString handlersInput = new inputString("");
    private Hashtable<String,Object> gameConfig;
    private Hashtable<String, Object> gameState;
    private VBox[] playerBoxList;
    private int activePlayer;
    private boolean gameTerminated;

    @Override
    public void start(Stage primaryStage) throws Exception
    {

        Platform.setImplicitExit(false);
        Font.loadFont(new URL("file:///"+Paths.get(dirPathToData,"fonts","Aka-AcidGR-GhostStory.ttf").toString()).toExternalForm(), 60);
        gameStage = primaryStage;
        createGameStage();
    }


    /**
     * Pass game configuration parameters and open the window
     * @param config
     */
    public void init(Hashtable<String,Object> config,Hashtable<String,Object> state) throws IOException {
        gameConfig = config;
        gameState = state;
        gameTerminated = false;
        update(state);
//        text.setText(((WordDictionary) state.get("hiddenWord")).getCurrentHiddenString());
        Platform.runLater(() -> gameStage.show());
    }


    /**
     * Method to update what is displayed in the game window.
     *
     * <p>
     * <b>Note:</b> Notice use of Pathform.runLater to be usable from a different thread.
     *
     * @param gameStatus
     */
    public void update(Hashtable<String, Object> gameStatus) throws IOException
    {
        Platform.runLater(() -> {


        if (activePlayer==0)
        {
            playerBoxList[activePlayer].setStyle(inactivePlayerStyle());
            activePlayer=1;
            playerBoxList[activePlayer].setStyle(activePlayerStyle());
        }
        else if (activePlayer==1)
        {
            playerBoxList[activePlayer].setStyle(inactivePlayerStyle());
            activePlayer=0;
            playerBoxList[activePlayer].setStyle(activePlayerStyle());
        }

        text.setText(
        ((WordDictionary)gameStatus.get("hiddenWord")).getCurrentHiddenString());

        hangman_img.setImage(
        getHangmanImages(((Life) gameStatus.get("lifes")).getCurrent()));

        input.requestFocus();

        if (!(boolean)gameStatus.get("play")) {endGame();}


    });
        if (gameTerminated) gameState.computeIfPresent("play",(k,v)->false);
    }

    /**
     * Pauses the main Thread to wait for user input through handling KeyEvent
     * @param input
     * @throws InterruptedException
     */
    public boolean input(inputString input) throws InterruptedException
    {
        handlersInput = input;
        synchronized(HangMan.hangman) {HangMan.hangman.wait();}
        return gameTerminated;
    }


    /**
     * Fire an Event to emulate an internal "window closure" event
     * @param
     */
    public void endGame() {Platform.runLater(() ->{try{

            TimeUnit.SECONDS.sleep(2);
            gameStage.fireEvent
                (new WindowEvent(gameStage, WindowEvent.WINDOW_CLOSE_REQUEST));

        }catch (InterruptedException e) {e.printStackTrace();}});}


    /**
     * Completely Terminate the javaFX platform and thus it's thread
     * <p>
     * <b>Note:</b> It's necessary since Platform.setImplicitExit() is set to FALSE
     */
    public void terminate()
    {
        Platform.runLater(() -> Platform.exit());
    }

    /*----------------------------------
      --- PRIVATE INTERNAL METHODS ---
    ------------------------------------*/

    /**
     * Setup the Stage/Layout for the main game window
     */
    private void createGameStage() throws IOException {

        gameStage.setTitle("Handman: Game");

        //--- SETTING UP INPUT PANEL ---

        input = new TextField();
        input.setStyle("-fx-background-color: white;");
        input.setOnKeyPressed(e -> handleInput(e));
        //input.setStyle("-fx-background-color: transparent;");

        BorderPane layoutBottom = new BorderPane();

        layoutBottom.setCenter(input);

        //--- SETTING UP CENTRAL GAME PANEL ---

        hangman_img  = new ImageView();
        hangman_img.setImage(getHangmanImages(-1));
        hangman_img.setCache(true);
        hangman_img.setFitWidth(200);
        hangman_img.setPreserveRatio(true);
        text = new Label();
        text.setStyle("-fx-background-color: transparent;"+
                      "-fx-font-size: 90;"+
                      "-fx-font-family: Aka-AcidGR-GhostStory;");
        //text.setFont(Font.font("Aka-AcidGR-RomanScript",60));
        text.setTextFill(Color.WHITESMOKE);
        text.setTextAlignment(TextAlignment.CENTER);

        BorderPane layoutCenter = new BorderPane();

        layoutCenter.setRight(hangman_img);
        layoutCenter.setCenter(text);

        //--- SETTING UP PLAYERS PANEL---

        playerBoxList = new VBox[2];
        playerBoxList[0] = makePlayer();
        playerBoxList[1] = makePlayer();
        HBox players = new HBox(playerBoxList[0],playerBoxList[1]);
        players.setSpacing(0);
        players.setAlignment(Pos.CENTER);

        activePlayer = 1;

        //--- SETTING UP GAME WINDOW LAYOUT ---

        BorderPane layout = new BorderPane();

        layout.setCenter(layoutCenter);
        layout.setBottom(layoutBottom);
        layout.setTop(players);

        layout.setBackground(new Background(
                             new BackgroundImage(
                             new Image ("file:///"+Paths.get(dirPathToData,"images","background.png").toString()),
                     null,null,null,null)));

        //--- SETTING UP SCENE ---

        Scene scene = new Scene(layout,800,400);


        //--- SETTING UP STAGE ---
        gameStage = new Stage();
        gameStage.setScene(scene);
        gameStage.setOnCloseRequest(e -> handleCloseRequest(e));

    }

    /**
     * Get the image corresponding to "idx" amount of lifes from data folder
     * @param idx
     * @return Image
     */
    private Image getHangmanImages(int idx)
    {
        if (idx<0) idx = getHangmanImages().length-1;
        return getHangmanImages()[idx];
    }

    /**
     * Get the list of images corresponding the amount of lifes from data folder
     * @return Image[]
     */
    private Image[] getHangmanImages()
    {
        File dir = new File(Paths.get(dirPathToData,"images","hangman_images").toString());

        File[] file_list = dir.listFiles (new FilenameFilter() {
            public boolean accept(File dir, String filename)
            { return filename.endsWith(".png"); }
        } );

        Image[] img_list = new Image[file_list.length];

        for(int i = 0;i<file_list.length;i++)
        {
            try {  img_list[i] = new Image ("file:///"+file_list[i].getCanonicalPath());  }
            catch (IOException e)  {e.printStackTrace();}
        }

        return img_list;
    }

    /**
     * Method for making a player box. To be updated when player class is implemented.
     * @return VBox
     */
    private VBox makePlayer()
    {
        Label label1 = new Label("player-1");
        Label label2 = new Label("Letters User: a, d, x, b, q, p, p, q, x, q, q, q , q ,q ,q ,q,q");
        label1.setFont(Font.font("Century Gothic",12));
        label2.setFont(Font.font("Century Gothic",12));
        label1.setTextFill(Color.BLACK);
        label2.setTextFill(Color.BLACK);
        label1.setTextAlignment(TextAlignment.CENTER);
        label2.setTextAlignment(TextAlignment.CENTER);
        VBox player = new VBox(label1,label2);
        player.setSpacing(2);

        player.setStyle(inactivePlayerStyle());

        return player;
    }


    /*----------------------------------
           --- EVENT HANDLERS ---
    ------------------------------------*/

    /**
     * Handles the event of entering a letter by returning the letter and unpausing the main thread
     * @param event
     */
    public void handleInput(KeyEvent event)
    {
        if (event.getCode().equals(KeyCode.ENTER))
        {
            handlersInput.set(input.getCharacters().toString());
            input.clear();
            synchronized(HangMan.hangman) {HangMan.hangman.notify();}
        }

    }

    /**
     * Handles the event of a closing a window or the emulation of such event.
     * @param event
     */
    public void handleCloseRequest (WindowEvent event)
    {
        gameTerminated = true;
        //gameConfig.computeIfPresent("exit", (k, v) -> true);
        synchronized(HangMan.hangman) {HangMan.hangman.notify();}
        gameStage.hide();

    }

    @Override
    public void handle(ActionEvent event)
    {

    }

    /*----------------------------------
      ---  STATIC PART OF THE CLASS ---
    ------------------------------------*/

    /**
     * Returns the default cssLayout for the active player VBox
     * @return cssLayout
     */
    public static String activePlayerStyle()
    {
        String cssLayout = "-fx-padding: 5;"
                + "-fx-border-width: 3;" + "-fx-border-insets: 2;"  + "-fx-border-style: solid inside;"
                + "-fx-border-radius: 2;" + "-fx-border-color: rgba(0,128,0,1);"
                + "-fx-background-color: rgba(190,235,190,0.4);" + "-fx-background-insets: 2;";

        return cssLayout;
    }

    /**
     * Returns the default cssLayout for the inactive players VBoxes
     * @return cssLayout
     */
    public static String inactivePlayerStyle()
    {
        String cssLayout = "-fx-padding: 5;" + "-fx-border-style: solid inside;"
                + "-fx-border-width: 1;" + "-fx-border-insets: 2;"
                + "-fx-border-radius: 1;" + "-fx-border-color: whitesmoke;"
                + "-fx-background-color: rgba(255,240,240,0.4);" + "-fx-background-insets: 2;";

        return cssLayout;
    }

}
