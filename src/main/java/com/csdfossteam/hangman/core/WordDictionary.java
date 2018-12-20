/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.csdfossteam.hangman.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Random;
import java.util.stream.Stream;

/**
 *
 * @author xrica_vabenee
 */
public class WordDictionary {

    private String st;
    public File f;
    private Path file_path;
    private BufferedReader br;
    private int listLength;
    private Random rand;
    private int select;
    private String current;
    private ArrayList<Character> currentHidden;

    public WordDictionary(String dict_file) throws FileNotFoundException, IOException
    {
        
        st = new String();
        file_path = Paths.get(new java.io.File( "." ).getCanonicalPath(), "src", dict_file);
        listLength = 0;
        rand = new Random();
        current = new String();
        currentHidden = new ArrayList<Character>();
        
        countWordList(); 
        
        
    }
    
    private void countWordList() throws IOException 
    {
        f = new File(file_path.toString());
        br = new BufferedReader(new FileReader(f));
        
        while ((st = br.readLine()) != null) {
            //System.out.println(st);
            listLength++;
        }
    }
    
    public void changeFile(String dict_file) throws IOException
    {
        file_path = Paths.get(new java.io.File( "." ).getCanonicalPath(), "src", dict_file);
        
        countWordList();   
    }

    

    public void pickRandomWord() throws IOException
    {
        select = rand.nextInt(listLength);
        // System.out.println(select);
        Stream<String> lines = Files.lines(Paths.get(new java.io.File( "." ).getCanonicalPath(), "src", "words.txt"));
        current = lines.skip(select).findFirst().get().toLowerCase();
       //System.out.println("The word which was selected is: "+line);

    }

    public String getCurrentString()
    {
        return current;
    }

    public ArrayList<Character> getCurrentHidden()
    {
        return currentHidden;
    }
    
    public String getCurrentHiddenString()
    {
        StringBuilder sb = new StringBuilder();
        for (Character s : currentHidden)
        {
            sb.append(s);
            sb.append(" ");
        }
        return sb.toString();
    }

    public void createDashes(boolean helpfulVersion)
    {
        if (helpfulVersion)
        {
            char firstLetter = current.charAt(0);
            currentHidden.add(firstLetter);
            // System.out.print(firstLetter);

            for (int i = 1; i < current.length() - 1; i++)
            {
                if (current.charAt(i) == firstLetter)
                {
                    currentHidden.add(firstLetter);
                } 
                else
                {
                    currentHidden.add('_');
                }
            }

            currentHidden.add(current.charAt(current.length()-1));
         
        }
        else 
        {
            for (int i = 0; i < current.length() - 2; i++) 
            {
                currentHidden.add('_');
            }
        }
    }

    
    public String getArrayListToString(ArrayList<Character> inputArray)
    {
        StringBuilder sb = new StringBuilder();
        for (Character s : inputArray)
        {
            sb.append(s);
            sb.append(" ");
        }
        return sb.toString();
    }
    
        public void printArrayList(ArrayList<Character> inputArray) {
        for (int i = 0; i < inputArray.size(); i++) {
            System.out.print(inputArray.get(i) + " ");
        }
    }


 
}
