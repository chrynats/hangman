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
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
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

import com.csdfossteam.hangman.core.*;

/**
 * <h1>Implements a Hangman GUI Class.</h1>
 *  *
 * <p>
 * <b>Note:</b> Multiplayer part is missing.
 *
 * @author  nasioutz
 * @version 0.8
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

    private TextField input;
    private Label text;
    private ImageView hangman_img;
    private Stage gameStage; //Public to be accessible from outside it's thread
    private Alert exitGameAlert;
    private VBox[] playerBoxList;

    private String dirPath = new java.io.File( "." ).getCanonicalPath();
    private String dirPathToData = Paths.get(dirPath,"data").toString();
    private inputString handlersInput = new inputString("");
    private Hashtable<String,Object> gameConfig,gameState;
    private int activePlayer;
    private boolean gameTerminated;
    private double xOffset = 0, yOffset = 0;

    private IntegerProperty scene_width = new SimpleIntegerProperty(this,"scene_width",850);
    private IntegerProperty scene_height = new SimpleIntegerProperty(this,"scene_height",400);

    @Override
    public void start(Stage primaryStage) throws Exception
    {

        Platform.setImplicitExit(false);

        Font.loadFont(
        new URL("file:///"+Paths.get(dirPathToData,"fonts","AC-DiaryGirl_Unicode.ttf").toString()).toExternalForm(), 60);

        gameStage = primaryStage;

    }


    /**
     * Pass game configuration parameters and open the window.
     * @param config
     * @param state
     */
    public void init(Hashtable<String,Object> config,Hashtable<String,Object> state) throws IOException
    {
        Platform.runLater(() -> createGameStage());
        gameConfig = config;
        gameState = state;
        gameTerminated = false;
        update(state);
        Platform.runLater(() ->
            gameStage.show());

    }

    /**
     * Pass game configuration parameters along with winodw size and open the window
     * @param config
     * @param state
     * @param width
     * @param height
     * @throws IOException
     */
    public void init(Hashtable<String,Object> config,Hashtable<String,Object> state,int width,int height) throws IOException
    {
        init(config,state);
        scene_width.set(width);
        scene_height.set(height);
    }


    /**
     * Method to update what is displayed in the game window.
     *
     * <p>
     * <b>Note:</b> Notice use of Pathform.runLater to be usable from a different thread.
     *
     * @param gameStatus
     */
    public void update(Hashtable<String, Object> gameStatus)
    {
        Platform.runLater(() -> {


        playerBoxList[activePlayer].getStyleClass().add("player-vbox-inactive");
        for (Node boxlabel : playerBoxList[activePlayer].getChildren())
            ((Label) boxlabel).getStyleClass().add("player-label-inactive");

        activePlayer=(activePlayer+1)%2;

        playerBoxList[activePlayer].getStyleClass().remove("player-vbox-inactive");
        for (Node boxlabel : playerBoxList[activePlayer].getChildren())
            ((Label) boxlabel).getStyleClass().remove("player-label-inactive");


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
     * Pauses the main Thread to wait for user input through handling KeyEvent.
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
     * Fire an Event to emulate an internal "window closure" event.
     * @param
     */
    public void endGame() {try{

        TimeUnit.MILLISECONDS.sleep(750);

        Platform.runLater(() ->
                gameStage.fireEvent (new WindowEvent(gameStage, WindowEvent.WINDOW_CLOSE_REQUEST)));

    }catch (InterruptedException e) {e.printStackTrace();}}

    public void closeGame()
    {
        Optional<ButtonType> result = exitGameAlert.showAndWait();
        if (((Optional) result).get()==ButtonType.OK)
        {
            gameConfig.computeIfPresent("exit", (k, v) -> true);
            endGame();
        }
    }


    /**
     * Completely Terminate the javaFX platform and thus it's thread.
     * <p>
     * <b>Note:</b> It's necessary since Platform.setImplicitExit() is set to FALSE.
     */
    public void terminate()
    {
        Platform.runLater(() -> Platform.exit());
    }

    /*----------------------------------
      --- PRIVATE INTERNAL METHODS ---
    ------------------------------------*/

    /**
     * Setup the Stage/Layout for the main game window.
     */
    private void createGameStage() {

        //--- SETTINGS UP DIALOG PANES ---

        exitGameAlert = new Alert(Alert.AlertType.CONFIRMATION);
        exitGameAlert.setTitle("End Game and Exit");
        exitGameAlert.setHeaderText("End Game and Exit Application");
        exitGameAlert.setContentText("Are you sure?");
        exitGameAlert.initStyle(StageStyle.UNDECORATED);

        DialogPane exitPane = exitGameAlert.getDialogPane();
        exitPane.getStylesheets().add(getClass().getResource("HangmanStylez.css").toExternalForm());
        exitPane.getStyleClass().add("exit-pane");
        exitPane.setGraphic(new ImageView(
                            findImage("close-white.png",20,20,true,true)));

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
        hangman_img.fitHeightProperty().bind((scene_height.multiply(0.825)));
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

        Region backRegion = new Region();
        Region playerRegion = new Region();
        HBox.setHgrow(backRegion, Priority.ALWAYS);
        HBox.setHgrow(playerRegion, Priority.ALWAYS);

        Button exitButton = new Button();
        exitButton.setGraphic(new ImageView(
                              findImage("close-white.png",20,20,true,true)));
        exitButton.getStyleClass().add("exit-button");
        exitButton.setOnAction(e -> closeGame());

        Button backButton = new Button();
        backButton.setGraphic(new ImageView(
                              findImage("back-white.png",20,20,true,true)));
        backButton.getStyleClass().add("exit-button");
        backButton.setOnAction(e -> endGame());

        HBox playersBox = new HBox(backButton,backRegion,playerBoxList[0],playerBoxList[1],playerRegion,exitButton);
        playersBox.getStyleClass().add("playersbox-hbox");

        activePlayer = 1;

        //--- SETTING UP GAME WINDOW LAYOUT ---

        BorderPane layout = new BorderPane();

        layout.setCenter(layoutCenter);
        layout.setBottom(layoutBottom);
        layout.setTop(playersBox);
        layout.setBackground(new Background(
                    new BackgroundImage(
                             findImage("background.png"),null,null,null,null)));

        //--- SETTING UP SCENE ---

        Scene scene = new Scene(layout,scene_width.get(),scene_height.get());
        scene_width.bind(scene.widthProperty());
        scene_height.bind(scene.heightProperty());
        scene.getStylesheets().add("/com/csdfossteam/hangman/face/gui/HangmanStylez.css");
        scene.setOnMousePressed(e-> getOffset(e));
        scene.setOnMouseDragged(e-> moveWindow(e));

        //--- SETTING UP STAGE ---
        gameStage = new Stage();
        gameStage.setScene(scene);
        gameStage.widthProperty().addListener((e) -> resizeText(e));
        gameStage.setOnCloseRequest(e -> handleCloseRequest(e));
        gameStage.initStyle(StageStyle.TRANSPARENT);
        gameStage.setTitle("Handman: Game");
        gameStage.getIcons().add(
        findImage("hangman-icon.png"));

    }

    /**
     * Get the image corresponding to "idx" amount of lifes from data folder.
     * @param idx
     * @return Image
     */
    private Image getHangmanImages(int idx)
    {
        if (idx<0) idx = getHangmanImages().length-1;
        return getHangmanImages()[idx];
    }

    /**
     * Get the list of images corresponding the amount of lifes from data folder.
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

    /**
     * Get an image from the designated data folder.
     * @param imageName
     * @return
     */
    private Image findImage(String imageName)
    {
        return new Image ("file:///"+Paths.get(dirPathToData,"images",imageName).toString());
    }

    /**
     * Get an image from the designated data folder with the specified parameters.
     * @param imageName
     * @param w
     * @param h
     * @param preserveRatio
     * @param smooth
     * @return
     */
    private Image findImage(String imageName, int w, int h, boolean preserveRatio, boolean smooth)
    {
        return (new Image ("file:///"+Paths.get(dirPathToData,"images",imageName).toString(),w,h,preserveRatio,smooth));
    }


    /*----------------------------------
           --- EVENT HANDLERS ---
    ------------------------------------*/

    /**
     * Handles the event of entering a letter by returning the letter and unpausing the main thread
     * @param event
     */
    private void handleInput(KeyEvent event)
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
    private void handleCloseRequest (WindowEvent event)
    {
        gameTerminated = true;
        synchronized(HangMan.hangman) {HangMan.hangman.notify();}
        Platform.runLater(()->gameStage.hide());
    }

    /**
     * Moves the Window Around
     * @param event
     */
    private void moveWindow(MouseEvent event)
    {
        gameStage.setX(event.getScreenX() - xOffset);
        gameStage.setY(event.getScreenY() - yOffset);
    }

    /**
     * Get the Current Window Position
     * @param event
     */
    private void getOffset(MouseEvent event)
    {
        xOffset = event.getSceneX();
        yOffset = event.getSceneY();
    }

    /**
     * Resize the hidden word label font size to fit in the window size
     * @param e
     */
    private void resizeText(Observable e)
    {
        Platform.runLater(() -> {
        Double fontSize = text.getFont().getSize();
        String clippedText = Utils.computeClippedText( text.getFont(), text.getText(), text.getWidth(), text.getTextOverrun(), text.getEllipsisString() );
        Font newFont;
        while ( !text.getText().equals( clippedText ) && fontSize > 0.5 )
        {
            fontSize = fontSize - 0.05;
            newFont = Font.font( text.getFont().getFamily(), fontSize);
            clippedText = Utils.computeClippedText( newFont, text.getText(), text.getWidth(), text.getTextOverrun(), text.getEllipsisString() );
        }
        text.setStyle("-fx-font-size:"+(fontSize-4)+"px");
     });}
    @Override
    public void handle(ActionEvent event)
    {

    }

    /*----------------------------------
      ---  STATIC PART OF THE CLASS ---
    ------------------------------------*/




}
