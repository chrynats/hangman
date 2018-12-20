/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.csdfossteam.hangman.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import static java.time.Clock.system;


/**
 * print
 *
 * @author xrica_vabenee
 */
public class Handler {

    /**
     * @param args the command line arguments
     * @throws java.io.FileNotFoundException
     */
    
       
    public Handler ()
    {
        
    }
    public void start() throws FileNotFoundException, IOException
    {
        
        
        //---- Start Up Interface ----
        
        DemoCLI itrface = new DemoCLI();
        
        /* Initialize and Launch the interface here */
        
        //---- Settings Part ----
        Boolean notExit = true;    
        
        do {
            
            //Select Word Pool

                  
            //Create A Game with Desired Settings
            
            String dict_file = "words.txt";
            GameMachanics game = new GameMachanics(dict_file);

            //Iniitalize game parameters that are outside the constructor?
            game.init();

            while (game.play())
            {               
                //Display Game State
                itrface.display(game.gameState());
                
                //Get input from Interface
                String c = itrface.input(); 
                game.inputLetter(c);               
            }
            
            notExit = false;
        
        } while (notExit);
    }

}
