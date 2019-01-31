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
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author User
 */
public class SocketServer {
    
    ServerSocket servSocket;
    Socket socket ;
    
    public void SocketServer(int port) throws IOException
    {
        servSocket = new ServerSocket(port);
        socket = servSocket.accept();
        
    }
    
    public String receiveByClient() throws IOException
    {
        BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String str = br.readLine();
        
        return str;
    }
    
    public void sendToClient(String str) throws IOException
    {
        OutputStreamWriter os = new OutputStreamWriter(socket.getOutputStream());
        PrintWriter out = new PrintWriter(os);
        out.write(str);
        out.flush();
    }
}
