/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.csdfossteam.hangman.core;


import java.io.IOException;


/**
 *
 * @author Aradon
 */
public class HangMan 
{

    public static Object hangman;

    public static void main(String[] args) throws IOException, InterruptedException
    {

        hangman = new Handler();
        
        ((Handler) hangman).start();
    }
}
