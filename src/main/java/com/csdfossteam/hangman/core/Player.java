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
    private Life lifes;
    private ArrayList<Character> wrongLetters;
    private boolean turn;
    private String letter;
    
    public Player(boolean t){
        lifes=new Life(6);
        wrongLetters=new ArrayList<>();
        turn=t;
        cli = new DemoCLI();
    }
    
    public Life getLifes(){
        return lifes;
    }
    
    public ArrayList<Character> getLetters(){
        return wrongLetters;
    }
    
    public boolean turn(){
        return turn;
    }
    
    public void reduceLifes(char letter){
        lifes.reduce();
        wrongLetters.add(letter);
    }

    public void setLifes(Life lifes) { this.lifes = lifes;}


}
