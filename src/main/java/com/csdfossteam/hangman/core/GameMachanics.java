/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.csdfossteam.hangman.core;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Scanner;

/**
 *
 * @author xrica_vabenee
 */
public class GameMachanics {

    
    private WordDictionary words;
    //private ArrayList<Character> hiddenWord;
    private Life life;


    public GameMachanics(String dict_file) throws FileNotFoundException, IOException {
        
        //hiddenWord = new ArrayList<Character>();
        
        words = new WordDictionary(new String(dict_file));
        //hiddenWord = words.getCurrentHiden()
        
        life = new Life(6);
    }
    
    public void init() throws IOException
    {
        words.pickRandomWord();
        words.createDashes(true);
        //dict.printArrayList(dict.getArrayList());
    }

    public boolean checkChar(String c) {
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

    public void inputLetter(String c) {

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
            //life.printLife();
        }

    }

    public boolean play() 
    {
        return !checkWord();
    }
    
    //return true otan prepei na teleiosei to game
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
    
    public Hashtable<String,Object> gameState()
    {
        Hashtable gameStatus = new Hashtable<String,Object>();
        gameStatus.put("hiddenWord",words);
        gameStatus.put("lifes",life);
        return gameStatus;
    }

}
