/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.csdfossteam.hangman.core;

import java.util.ArrayList;

/**
 *
 * @author User
 */
public class Player {
    private String name;
    private Life lifes;
    private ArrayList<Character> wrongLetters;
    private String letter;
    
    public Player(String nm){
        name = nm;
        lifes=new Life();
        wrongLetters=new ArrayList<>();
    }
    
    public Life getLifes(){
        return lifes;
    }

    public String getName() {return name;}
    
    public ArrayList<Character> getLetters()
    {
        return wrongLetters;
    }

    public boolean hasLetter(char letter)
    {
        return wrongLetters.contains(letter);
    }

    public void reset()
    {
        lifes = new Life();
        wrongLetters = new ArrayList<>();
    }

    public void reduceLifes(char letter)
    {
        lifes.reduce();
        if (!hasLetter(letter))
        {wrongLetters.add(letter);}
    }



}
