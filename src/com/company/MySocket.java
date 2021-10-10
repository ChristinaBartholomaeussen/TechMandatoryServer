package com.company;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.TimeZone;

public class MySocket {

    private final String path = "C:/Users/cmbar/Desktop/server/";
    private ServerSocket welcomeSocket;
    private Socket connection;
    private BufferedReader inFromClient;
    private DataOutputStream outToClient;
    private String input;
    private StringTokenizer token;
    private String wordFromClient;
    private final int port = 5000;


    public void createSocket() {

        try {
            System.out.println("\nstarting server...");
            welcomeSocket = new ServerSocket(port);
            System.out.println("we have a socket");


        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void connectionFound() {
        try {

            System.out.println("\nwaiting for connection...");
            connection = welcomeSocket.accept();
            System.out.println("we have a connection");
            inFromClient = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            outToClient = new DataOutputStream(connection.getOutputStream());

        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void readClientInput() {
        try {

            input = inFromClient.readLine();
            System.out.println("FROM CLIENT: " + input + '\n');
            token = new StringTokenizer(input);

            if (token == null) {
                welcomeSocket.close();
            }

        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (NullPointerException e) {
            System.out.println(e.getMessage());
        }
    }

    public void readFileOutput() {

        FileInputStream fileInputStream;

        try {
            wordFromClient = token.nextToken(); //Læser GET som første ord
            wordFromClient = token.nextToken();
        } catch (NoSuchElementException e) {
            System.out.println(e.getMessage());
        }

        File file;
        String filnavn = "";

        if (wordFromClient.equals("/")) {

            file = new File(path + "/index.html");

        } else {
            filnavn = wordFromClient.substring(1);
            file = new File(path + filnavn);

            if (!file.exists()) {
                file = new File(path + "notFound.html");
            }
        }

        try {

            fileInputStream = new FileInputStream(file);

            boolean read = true;

            int i = 0;

            byte[] bytes = new byte[fileInputStream.available()];

            while (read) {

                i = fileInputStream.read(bytes);

                if (i == -1) {
                    read = false;
                }
                if (file.getName().contains("notFound")) {


                    System.out.println("HTTP/1.0 404 PAGE NOT FOUND");
                    outToClient.writeBytes("HTTP/1.0 404 PAGE NOT FOUND\n");
                    System.out.println("Date: " + getServerTime());
                    outToClient.writeBytes("Date: " + getServerTime() + '\n');
                    System.out.println("Type: " + fileContent(file));
                    outToClient.writeBytes("Type: " + fileContent(file) + '\n');
                    System.out.println("Content-length: " + file.length());
                    outToClient.writeBytes("Content-length: " + file.length() + '\n');
                    outToClient.write('\n');

                    outToClient.write(bytes);
                    connection.close();

                    read = false;

                }
                if (file.exists()) {

                    System.out.println("HTTP/1.0 200 OK");
                    outToClient.writeBytes("HTTP/1.0 200 OK\n");
                    System.out.println("Date: " + getServerTime());
                    outToClient.writeBytes("Date: " + getServerTime() + '\n');
                    System.out.println("Type: " + fileContent(file));
                    outToClient.writeBytes("Type: " + fileContent(file) + '\n');
                    System.out.println("Content-length: " + file.length());
                    outToClient.writeBytes("Content-length: " + file.length() + '\n');
                    outToClient.write('\n');

                    outToClient.write(bytes);
                    connection.close();

                    read = false;
                }

            }
        } catch (FileNotFoundException e) {
            System.out.println("Error: " + e.getMessage());

        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }


    public String getServerTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "EEE, dd MMM yyyy HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        return dateFormat.format(calendar.getTime());
    }

    public String fileContent(File file) {
        return URLConnection.guessContentTypeFromName(file.getName());

    }

}