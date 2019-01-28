/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.csdfossteam.hangman.core;

import java.util.Hashtable;
import java.util.Scanner;

/**
 *
 * @author Aradon
 */
public class DemoCLI 
{
    private Scanner scan;
    
    public DemoCLI()
    {
        scan = new Scanner(System.in);
    }
    
    public String input()
    {
        System.out.println("--------------");
        System.out.print("give letter: ");
        String c = scan.nextLine();
        return c;
    }

    public void display(Hashtable<String, Object> gameStatus) 
    {
        
        
        System.out.println("\n---------------");
        System.out.println("Current Life: "+((Life) gameStatus.get("lifes")).getCurrentString());      
        System.out.println("---------------");
        System.out.println(((WordDictionary)gameStatus.get("hiddenWord")).getCurrentHiddenString());
    }
    
}
