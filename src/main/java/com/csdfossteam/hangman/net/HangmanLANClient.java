package com.csdfossteam.hangman.net;

import com.csdfossteam.hangman.core.Player;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Hashtable;

public class HangmanLANClient
{
    BufferedReader in;
    PrintWriter out;
    Socket remoteServerSocket;


    public HangmanLANClient(String ip, int port) throws IOException {

        remoteServerSocket = new Socket(ip,port);
        in = new BufferedReader(new InputStreamReader(remoteServerSocket.getInputStream()));
        out = new PrintWriter(remoteServerSocket.getOutputStream());
    }


    public void sendToServer(String str) throws IOException
    {
        out.println(str);
        out.flush();
    }
    public void sendObjectToServer(Object ob) throws IOException
    {
        ObjectOutputStream os = new ObjectOutputStream(remoteServerSocket.getOutputStream());
        ObjectInputStream is = new ObjectInputStream(remoteServerSocket.getInputStream());

        os.writeObject(ob);
    }
    public String receiveFromServer() throws IOException
    {
        String str = in.readLine();

        return str;
    }
    public Object receiveObjectFromServer() throws IOException, ClassNotFoundException {
        ObjectOutputStream os = new ObjectOutputStream(remoteServerSocket.getOutputStream());
        ObjectInputStream is = new ObjectInputStream(remoteServerSocket.getInputStream());

        return is.readObject();
    }


    public static void runClientDemo() throws IOException, ClassNotFoundException {

        boolean exit = false;
        HangmanLANClient client = new HangmanLANClient("192.168.1.21",6666);

        while(!exit) {
            System.out.println("Waiting Server Command");
            String data = client.receiveFromServer();
            if (data.equals("play"))
            {
                System.out.println("Waiting for Object");
                Hashtable<String,Object> config = (Hashtable) client.receiveObjectFromServer();
                ((ArrayList<Player>) ((Hashtable<String, Object>)config).get("playerList")).add(new Player("player3"));
                System.out.println("Sending Object");
                client.sendObjectToServer(config);

            }
            else if (data.equals(("not")))
            {
                client.sendToServer("Weakling!");
            }
            else if (data.equals("."))
            {
                client.sendToServer("Leave and let Live?");
                exit = true;
            }
            else
            {
                client.sendToServer("Unknown Command!");
            }
        }

    }

    public static void main (String[] args) throws IOException, ClassNotFoundException
    {
        HangmanLANServer.runServerDemo();
    }


}
