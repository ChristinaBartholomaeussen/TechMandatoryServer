package com.company;

public class Main {
    public static void main(String[] args) {

        boolean keep_alive = true;


        MySocket mySocket = new MySocket();

        mySocket.createSocket();

        do {
            mySocket.connectionFound();
            mySocket.readClientInput();
            mySocket.readFileOutput();

        } while (keep_alive);

    }
}


