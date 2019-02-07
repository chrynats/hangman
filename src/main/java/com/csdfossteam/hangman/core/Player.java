/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.csdfossteam.hangman.core;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * A Simple class implementing the player entity
 *
 * Note: Made serializable for socket handling
 * @author User
 */
public class Player implements Serializable {



    private String name;
    private String remoteTag;
    private Life lifes;
    private ArrayList<Character> wrongLetters;
    private String letter;
    private int remoteIndex;


    public Player(String nm, int remote){
        this(nm);
        remoteIndex = remote;
        remoteTag = "Remote";
    }
    public Player(String nm){
        name = nm;
        lifes=new Life();
        wrongLetters=new ArrayList<>();
        remoteIndex = -1;
        remoteTag = "Local";
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

    public int getRemoteIndex()
    {
        return remoteIndex;
    }

    public String getRemoteTag() {return remoteTag;}



}
