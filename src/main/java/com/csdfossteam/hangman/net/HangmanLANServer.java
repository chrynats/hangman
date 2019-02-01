/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.csdfossteam.hangman.net;

import com.csdfossteam.hangman.core.GameEngine;
import com.csdfossteam.hangman.core.Player;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Scanner;

import static com.csdfossteam.hangman.face.cli.DemoCLI.isValidChoice;

/**
 *
 * @author User
 */
public class HangmanLANServer extends Thread {

    ServerSocket serverSocket;
    ArrayList<serverThread> clientThreadtList;


    public HangmanLANServer(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        clientThreadtList = new ArrayList<>();
    }


    public int findClient() throws IOException {


        clientThreadtList.add(new serverThread(serverSocket.accept()));

        return (clientThreadtList.size() - 1);

    }


    public Socket getClient(int i)
    {
        return clientThreadtList.get(i).getClient();
    }

    public String receiveFromClient(int i) throws IOException
    {
        String str = clientThreadtList.get(i).receiveFromClient();
        return str;
    }

    public void sendToClient(int i, String str) throws IOException
    {
        clientThreadtList.get(i).sendToClient(str);
    }

    public Object receiveObjectFromClient(int i) throws IOException, ClassNotFoundException
    {
        return clientThreadtList.get(i).receiveObjectFromClient();
    }

    public void sendObjectToClient(int i,Object ob) throws IOException
    {
        clientThreadtList.get(i).sendObjectToClient(ob);
    }


    public int getServerPort() {
        return serverSocket.getLocalPort();
    }

    public String getServerIP() throws UnknownHostException {
        return InetAddress.getLocalHost().getHostAddress();
    }

    public int getClientNumber() {
        return clientThreadtList.size();
    }

    public void freeClients() throws IOException {
        for (serverThread sThr : clientThreadtList)
        {
            sThr.close();
        }

        clientThreadtList.clear();
        serverSocket.close();
    }

    public static void runServerDemo() throws IOException, ClassNotFoundException {
        Scanner scan = new Scanner(System.in);

        HangmanLANServer serv = new HangmanLANServer(6666);
        System.out.println(serv.getServerIP());


        int clientIndex = serv.findClient();

        boolean exit = false;
        String select;

        do {
            do {
                System.out.println("Found Client in IP: " + serv.getClient(clientIndex).getInetAddress());
                System.out.println("1.Play | 2.Don't Play | 3. Exit");
                select = scan.nextLine();
            } while (!isValidChoice(select, 1, 4));

            if (Integer.parseInt(select) == 1) {
                serv.sendToClient(clientIndex, "play");
                Hashtable<String, Object> gameConfig = GameEngine.defaultConfig();
                System.out.println("Sending...");
                serv.sendObjectToClient(clientIndex, gameConfig);
                System.out.println("Receiving...");
                gameConfig = (Hashtable<String, Object>) serv.receiveObjectFromClient(clientIndex);
                System.out.println(((ArrayList<Player>) gameConfig.get("playerList")).size());
                //System.out.println(gameConfig.get("PlayerNumber"));

            } else if (Integer.parseInt(select) == 2) {
                serv.sendToClient(clientIndex, "not");
                String data = serv.receiveFromClient(clientIndex);
                System.out.println("Response: " + data);
            } else if (Integer.parseInt(select) == 3) {
                serv.sendToClient(clientIndex, ".");
                exit = true;
            }

        } while (!exit);
    }


    public static class serverThread {

        private Socket clientSocket;

        public serverThread(Socket client)
        {
            clientSocket = client;
        }

        public Socket getClient()
        {
            return clientSocket;
        }

        public String receiveFromClient() throws IOException {
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String str = in.readLine();

            return str;
        }

        public void sendToClient(String str) throws IOException {
            PrintWriter out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            out.println(str);
            out.flush();
        }

        public Object receiveObjectFromClient() throws IOException, ClassNotFoundException {
            ObjectOutputStream os = new ObjectOutputStream(clientSocket.getOutputStream());
            ObjectInputStream is = new ObjectInputStream(clientSocket.getInputStream());

            return is.readObject();
        }

        public void sendObjectToClient(Object ob) throws IOException {
            ObjectOutputStream os = new ObjectOutputStream(clientSocket.getOutputStream());
            ObjectInputStream is = new ObjectInputStream(clientSocket.getInputStream());

            os.writeObject(ob);
        }

        public void close() throws IOException {
            clientSocket.close();
        }







    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        HangmanLANServer.runServerDemo();
    }
}
