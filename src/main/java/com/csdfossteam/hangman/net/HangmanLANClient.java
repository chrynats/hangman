/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.csdfossteam.hangman.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

/**
 *
 * @author User
 */
public class HangmanLANClient {
    Socket socket;
    String ip;
    int port;
    
    public HangmanLANClient(String ip, int port) throws IOException{
        this.ip = ip;
        this.port = port;
        socket = new Socket(ip,port);
    }
    
    public void sendToServer(String str) throws IOException
    { 
        OutputStreamWriter os = new OutputStreamWriter(socket.getOutputStream());
        PrintWriter out = new PrintWriter(os);
        os.write(str);
        os.flush();
    }
    
    public String receiveByServer() throws IOException
    {
        
        BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String str = br.readLine();
        
        return str;
    }
    
    public String getIP()
    {
        return ip;
    }
    
    public int getPort()
    {
        return port;
    }
    
}
