/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.csdfossteam.hangman.face.cli;

import com.csdfossteam.hangman.core.GameEngine;
import com.csdfossteam.hangman.core.Life;
import com.csdfossteam.hangman.core.WordDictionary;
import com.csdfossteam.hangman.core.inputString;

import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Scanner;

/**
 *
 * @author Aradon
 */
public class DemoCLI 
{
    private Scanner scan;
    private String select;
    private boolean configured;
    
    public DemoCLI()
    {
        scan = new Scanner(System.in);
    }


    public Hashtable<String,Object> config () throws IOException {

        configured = false;

        Hashtable<String,Object> configuration = new Hashtable<String,Object>();
        do
        {

            do {
                clearConsole();
                System.out.println("\n---------------");
                System.out.println("1)Start Game");
                System.out.println("2)Configure Game");
                System.out.println("3)Exit");
                System.out.print("\nMake a selection: ");
                select = scan.nextLine();
                //System.out.println(isValidChoice(select,1,3));
            }
            while (!isValidChoice(select,1,4));



            if (Integer.parseInt(select)==1)
            {
                if (configured)
                    return configuration;
                else
                {
                    return GameEngine.defaultConfig();
                }
            }
            else if (Integer.parseInt(select)==2)
            {
                File[] dict_list = WordDictionary.getDictionaries();
                do
                    {
                        clearConsole();
                        for (int i = 0; i < dict_list.length; i++)
                        {
                            System.out.println("\n---------------");
                            System.out.println("\nAvailable Dictionaries");
                            System.out.println((i + 1)+")"+dict_list[i].getName().toUpperCase());
                        }
                        System.out.print("\nMake a selection: ");
                        select = scan.nextLine();
                    } while (!isValidChoice(select, 1, dict_list.length));

                configuration.put("dict_path",WordDictionary.getDictionaries()[Integer.parseInt(select)-1].toPath());

                if (configuration.containsKey("exit"))
                    configuration.computeIfPresent("exit",(k,v)->false);
                else
                    configuration.put("exit",false);

            }
            else if (Integer.parseInt(select)==3)
            {
                if (configuration.containsKey("exit"))
                    configuration.computeIfPresent("exit",(k,v)->true);
                else
                    configuration.put("exit",true);
                return configuration;
            }


        } while (!configured);
        return configuration;
    }

    public void init(Hashtable<String,Object> config,Hashtable<String,Object> state)
    {
        update(state);
    }

    public void input(inputString input)
    {
        System.out.println("--------------");
        System.out.print("give letter: ");
        String c = scan.nextLine();
        input.set(c);
    }

    public void update(Hashtable<String, Object> gameStatus)
    {
        System.out.println("\n---------------");
        System.out.println("Current Life: "+((Life) gameStatus.get("lifes")).getCurrentString());
        System.out.println("---------------");
        System.out.println(((WordDictionary)gameStatus.get("hiddenWord")).getCurrentHiddenString());
    }


    public static boolean isNumeric(String strNum) {
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException | NullPointerException nfe) {
            return false;
        }
        return true;
    }

    public static boolean isValidChoice(String select,int range1,int range2)
    {
        return DemoCLI.isNumeric(select) && Integer.parseInt(select) >= range1 && Integer.parseInt(select) <= range2;
    }

    public final static void clearConsole()
    {
        try
        {
            final String os = System.getProperty("os.name");

            if (os.contains("Windows"))
            {
                Runtime.getRuntime().exec("cls");
            }
            else
            {
                Runtime.getRuntime().exec("clear");
            }
        }
        catch (final Exception e)
        {
            //  Handle any exceptions.
        }
    }



}
