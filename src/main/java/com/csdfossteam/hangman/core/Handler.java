/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.csdfossteam.hangman.core;

import java.io.IOException;
import java.util.Hashtable;

import com.csdfossteam.hangman.face.cli.DemoCLI;
import com.csdfossteam.hangman.face.gui.HangmanGUI;
import javafx.application.Application;


/**
 * print
 *
 * @author xrica_vabenee, nasioutz
 */
public class Handler {

    /**
     * @param args the command line arguments
     * @throws java.io.FileNotFoundException
     */
    
    private DemoCLI cli;
    private HangmanGUI gui;

    private Thread gameEngineThread;
    private inputString inputBuffer;
    private Hashtable<String,Object> gameConfig;
    private Hashtable<String,Object> gameState;
    private GameEngine game;

    /**
     * Game Handler Constructor
     */
    public Handler ()
    {
        gameEngineThread = Thread.currentThread();
        inputBuffer = new inputString();
        game = new GameEngine();
        cli = new DemoCLI();
        gui = (HangmanGUI) HangmanGUI.startGUIThread();
    }

    /**
     * Main Method that Initiates Play
     * @throws IOException
     * @throws InterruptedException
     */
    public void start() throws IOException, InterruptedException {


        do {

            /*---- SETTINGS PART ----*/

            //Start the Settings Window
            gameConfig = cli.config();
            //gameConfig = GameEngine.defaultConfig();

            /*------ GAME PART ------*/

            //Initialize game parameters that are outside the constructor?
            gameState = game.init(gameConfig);


            //Start Game Window GUI
            if (game.play()) {
                gui.init(gameConfig, gameState);} //cli.init(gameConfig,gameState);

                while (game.play()) {


                    //Get input from Interface
                    gui.input(inputBuffer); //cli.input(inputBuffer);

                    //Transfer user input to game engine
                    game.inputLetter(inputBuffer.get());

                    //Update User Interface
                    gui.update(gameState); //cli.update(game.gameState());


                }

            }
            while (!(boolean) gameConfig.get("exit")) ;

            gui.terminate();

        }
}
