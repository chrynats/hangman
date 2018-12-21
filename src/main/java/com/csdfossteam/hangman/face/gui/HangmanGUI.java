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
import java.util.concurrent.TimeUnit;

import com.csdfossteam.hangman.core.HangMan;
import com.csdfossteam.hangman.core.Life;
import com.csdfossteam.hangman.core.WordDictionary;
import com.csdfossteam.hangman.core.inputString;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
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
    private double xOffset = 0;
    private double yOffset = 0;

    @Override
    public void start(Stage primaryStage) throws Exception
    {

        Platform.setImplicitExit(false);

        Font.loadFont(new URL("file:///"+Paths.get(dirPathToData,"fonts","AC-Serif.ttf").toString()).toExternalForm(), 60);

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


        playerBoxList[activePlayer].getStyleClass().add("player-vbox-inactive");
        for (Node boxlabel : playerBoxList[activePlayer].getChildren())
            ((Label) boxlabel).getStyleClass().add("player-label-inactive");

        activePlayer=(activePlayer+1)%2;
        playerBoxList[activePlayer].getStyleClass().remove("player-vbox-inactive");
        for (Node boxlabel : playerBoxList[activePlayer].getChildren())
            ((Label) boxlabel).getStyleClass().remove("player-label-inactive");
            //((Label) boxlabel).getStyleClass().add("player-label-active");


        text.setText(
        ((WordDictionary)gameStatus.get("hiddenWord")).getCurrentHiddenString());



        hangman_img.setImage(
        getHangmanImages(((Life) gameStatus.get("lifes")).getCurrent()));

        input.requestFocus();
        gameStage.toFront();

    });

        if (!(boolean)gameStatus.get("play")) {endGame();}
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
    public void endGame() {try{

        TimeUnit.MILLISECONDS.sleep(750);

        Platform.runLater(() ->
                gameStage.fireEvent (new WindowEvent(gameStage, WindowEvent.WINDOW_CLOSE_REQUEST)));

    }catch (InterruptedException e) {e.printStackTrace();}}


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

        //--- SETTING UP INPUT PANEL ---

        input = new TextField();
        input.getStyleClass().add("input-textfield");
        input.setOnKeyPressed(e -> handleInput(e));

        BorderPane layoutBottom = new BorderPane();

        layoutBottom.setCenter(input);

        //--- SETTING UP CENTRAL GAME PANEL ---

        hangman_img  = new ImageView();
        hangman_img.setImage(getHangmanImages(-1));
        hangman_img.setCache(true);
        hangman_img.setFitHeight(330);
        hangman_img.setPreserveRatio(true);

        text = new Label();
        text.getStyleClass().add("hiddenword-label");

        BorderPane.setAlignment(text,Pos.BOTTOM_LEFT);
        BorderPane.setMargin(text,new Insets(0,0,20,50));

        BorderPane layoutCenter = new BorderPane();

        layoutCenter.setRight(hangman_img);
        layoutCenter.setCenter(text);

        BorderPane.setAlignment(layoutCenter, Pos.BOTTOM_CENTER);

        //--- SETTING UP PLAYERS PANEL---

        playerBoxList = new VBox[2];
        playerBoxList[0] = makePlayer();
        playerBoxList[1] = makePlayer();

        Region placeholderRegion = new Region();
        Region playerRegion = new Region();
        HBox.setHgrow(placeholderRegion, Priority.ALWAYS);
        HBox.setHgrow(playerRegion, Priority.ALWAYS);

        Button exitButton = new Button();
        exitButton.setGraphic(new ImageView(
                              new Image ("file:///"+Paths.get(dirPathToData,"images","close-white.png").toString(),20,20,true,true)));

        exitButton.getStyleClass().add("exit-button");
        exitButton.setOnAction(e -> endGame());
        HBox playersBox = new HBox(placeholderRegion,playerBoxList[0],playerBoxList[1],playerRegion,exitButton);
        playersBox.getStyleClass().add("playersbox-hbox");

        activePlayer = 1;

        //--- SETTING UP GAME WINDOW LAYOUT ---

        BorderPane layout = new BorderPane();

        layout.setCenter(layoutCenter);
        layout.setBottom(layoutBottom);
        layout.setTop(playersBox);
        layout.setBackground(new Background(
                             new BackgroundImage(
                             new Image ("file:///"+Paths.get(dirPathToData,"images","background.png").toString()),
                     null,null,null,null)));

        //--- SETTING UP SCENE ---

        Scene scene = new Scene(layout,800,400);
        scene.getStylesheets().add("/com/csdfossteam/hangman/face/gui/HangmanStylez.css");
        scene.setOnMousePressed(e-> getOffset(e));
        scene.setOnMouseDragged(e-> moveWindow(e));

        //--- SETTING UP STAGE ---
        gameStage = new Stage();
        gameStage.setScene(scene);
        gameStage.setOnCloseRequest(e -> handleCloseRequest(e));
        gameStage.initStyle(StageStyle.TRANSPARENT);
        gameStage.setTitle("Handman: Game");

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
        Label label2 = new Label("Letters User: a, d, x, b, q, p, p, q, x");
        label1.getStyleClass().add("player-label-active");
        label2.getStyleClass().add("player-label-active");

        VBox player = new VBox(label1,label2);
        player.getStyleClass().add("player-vbox-active");

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
        Platform.runLater(()->gameStage.hide());

    }

    /**
     * Moves the Window Around
     * @param event
     */
    public void moveWindow(MouseEvent event)
    {
        gameStage.setX(event.getScreenX() - xOffset);
        gameStage.setY(event.getScreenY() - yOffset);
    }

    /**
     * Get the Current Window Position
     * @param event
     */
    public void getOffset(MouseEvent event)
    {
        xOffset = event.getSceneX();
        yOffset = event.getSceneY();
    }
    @Override
    public void handle(ActionEvent event)
    {

    }

    /*----------------------------------
      ---  STATIC PART OF THE CLASS ---
    ------------------------------------*/




}
