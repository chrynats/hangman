<<<<<<< HEAD
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.csdfossteam.hangman.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Scanner;

/**
 * Game Engine Class
 * Handles the words, lifes and letters of the player(s)
 *
 * @author xrica_vabenee, nasioutz
 */
public class GameEngine {

    private WordDictionary words;
    private Life life;
    Hashtable<String,Object> gameConfig;
    Hashtable<String,Object> gameState;




    public GameEngine()
    {
        gameState = new Hashtable<String,Object>();
    }

    /**
     * Take the configuration and create what's needed.
     * Currently:
     * <b>Making a dashed word from the WordDictionary</b>
     *
     * @param config
     * @throws IOException
     */
    public Hashtable<String,Object> init(Hashtable<String,Object> config) throws IOException
    {
        gameState.compute("play",(k,v) -> !(boolean) config.get("exit"));
        if (play()){
        life = new Life(6);
        words = new WordDictionary((Path)config.get("dict_path"));
        words.pickRandomWord();
        words.createDashes(true);
        gameConfig = config;
        gameState.put("hiddenWord",words);
        gameState.put("lifes",life);
        gameState.put("test-bool",true);
        }
        return gameState;
    }

    /**
     * Checks if character is valid
     * @param c
     * @return boolean
     */
    public boolean checkChar(String c)
    {
        if(c.isEmpty())
        {
            return false;
        }
        char charAt0 = c.charAt(0);
        if (Character.isLetter(charAt0) && c.length() == 1)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Registers the String input from the user and modifies game status
     * @param c
     */
    public void inputLetter(String c) {try{

        c = c.toLowerCase();
        boolean key=false;
        if (checkChar(c)) 
        { 
            for (int i = 0; i < words.getCurrentHidden().size(); i++)
            {
                if (c.charAt(0) == words.getCurrentString().charAt(i))
                {
                    words.getCurrentHidden().add(i, c.charAt(0));
                    words.getCurrentHidden().remove(i + 1);
                    key=true;
                }
            }
            
            if(!key) life.reduce();
        }

        updateGameStatus();

    } catch (NullPointerException e) {updateGameStatus();}}
    /**
     * Check whether the Game should continue
     */
    public boolean play()
    {
        return (boolean) gameState.get("play");
    }

    /**
     *  Forcefully Terminate the Game Status
     */
    public void terminatePlay()
    {
        gameState.computeIfPresent("play",(k,v) -> false);
    }

    /**
     * Updates game parameters
     * @return boolean
     */
    public void updateGameStatus()
    {
        gameState.computeIfPresent("play",(k,v) -> !checkWord() && !(boolean) gameConfig.get("exit"));
    }

    /**
     * Confirm if the word is completed
     * @return boolean
     */
    public boolean checkWord() {
        if(life.getCurrent()<=0)
        {
            return true;
        }
        for (int i = 0; i < words.getCurrentHidden().size(); i++) {
            if (words.getCurrentHidden().get(i) != words.getCurrentString().charAt(i)) {
                return false;
            }

        }
        return true;
    }

    /**
     * Return a Hashtable containing all info needed to be communicated to UI and Sockets
     * @return Hashtable<String,Object>
     */
    public Hashtable<String,Object> gameState()
    {
        return gameState;
    }

    /**
     * Returns a default configuration for quick game
     *
     * <b>reference for what the UI classes need to implement<b/>
     *
     * @return Hashtable<String,Object>
     * @throws IOException
     */
    public static Hashtable<String,Object> defaultConfig() throws IOException {

        Hashtable<String,Object> configuration = new Hashtable<String,Object>();
        configuration.put("dict_path",WordDictionary.getDictionaries()[0].toPath());
        configuration.put("exit",false);

        return configuration;
    }

    }
=======
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.csdfossteam.hangman.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Scanner;

/**
 * Game Engine Class
 * Handles the words, lifes and letters of the player(s)
 *
 * @author xrica_vabenee, nasioutz
 */
public class GameEngine {

    private WordDictionary words;
    private Life life;
    Hashtable<String,Object> gameConfig;
    Hashtable<String,Object> gameState;
    ArrayList<Player> playerList;
    private int playIndex;



    public GameEngine()
    {
        gameState = new Hashtable<String,Object>();
        playerList = new ArrayList<>();
        playIndex = 0;
    }

    public void addPlayersList(ArrayList<Player> plList )
    {
        for(int i=0; i<plList.size(); i++)
        {
            playerList.add(plList.get(i));
        }
    }

    /**
     * Take the configuration and create what's needed.
     * Currently:
     * <b>Making a dashed word from the WordDictionary</b>
     *
     * @param config
     * @throws IOException
     */
    public Hashtable<String,Object> init(Hashtable<String,Object> config) throws IOException
    {
        gameState.compute("play",(k,v) -> !(boolean) config.get("exit"));
        if (play()){
        life = new Life(6);
        words = new WordDictionary((Path)config.get("dict_path"));
        words.pickRandomWord();
        words.createDashes(true);
        gameConfig = config;
        gameState.put("hiddenWord",words);
        gameState.put("lifes",life);
        gameState.put("test-bool",true);
        gameState.put("playerList", playerList );
        }
        return gameState;
    }

    /**
     * Checks if character is valid
     * @param c
     * @return boolean
     */
    public boolean checkChar(String c)
    {
        if(c.isEmpty())
        {
            return false;
        }
        char charAt0 = c.charAt(0);
        if (Character.isLetter(charAt0) && c.length() == 1)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Registers the String input from the user and modifies game status
     * @param c
     */
    public void inputLetter(String c) {try{

        c = c.toLowerCase();
        boolean key=false;
        if (checkChar(c)) 
        { 
            for (int i = 0; i < words.getCurrentHidden().size(); i++)
            {
                if (c.charAt(0) == words.getCurrentString().charAt(i))
                {
                    words.getCurrentHidden().add(i, c.charAt(0));
                    words.getCurrentHidden().remove(i + 1);
                    key=true;
                }
            }
            
            if(!key)
            {
                gameState.get("playerList").get(playIndex).reduceLifes(c.charAt(0));
                changePlayerIndex();
            }
        }

        updateGameStatus();

    } catch (NullPointerException e) {updateGameStatus();}}

    private void changePlayerIndex()
    {
        if(playIndex == playerList.size()-1 || playerIndex<0)
        {
            playerIndex=0;
        }else
        {
            playerIndex++;
        }

    }

    /**
     * Check whether the Game should continue
     */
    public boolean play()
    {
        return (boolean) gameState.get("play");
    }

    /**
     *  Forcefully Terminate the Game Status
     */
    public void terminatePlay()
    {
        gameState.computeIfPresent("play",(k,v) -> false);
    }

    /**
     * Updates game parameters
     * @return boolean
     */
    public void updateGameStatus()
    {
        gameState.computeIfPresent("play",(k,v) -> !checkWord() && !(boolean) gameConfig.get("exit"));
    }

    /**
     * Confirm if the word is completed
     * @return boolean
     */
    public boolean checkWord() {
        if(life.getCurrent()<=0)
        {
            return true;
        }
        for (int i = 0; i < words.getCurrentHidden().size(); i++) {
            if (words.getCurrentHidden().get(i) != words.getCurrentString().charAt(i)) {
                return false;
            }

        }
        return true;
    }

    /**
     * Return a Hashtable containing all info needed to be communicated to UI and Sockets
     * @return Hashtable<String,Object>
     */
    public Hashtable<String,Object> gameState()
    {
        return gameState;
    }

    /**
     * Returns a default configuration for quick game
     *
     * <b>reference for what the UI classes need to implement<b/>
     *
     * @return Hashtable<String,Object>
     * @throws IOException
     */
    public static Hashtable<String,Object> defaultConfig() throws IOException {

        Hashtable<String,Object> configuration = new Hashtable<String,Object>();
        configuration.put("dict_path",WordDictionary.getDictionaries()[0].toPath());
        configuration.put("exit",false);

        return configuration;
    }

    }
>>>>>>> upstream/master
