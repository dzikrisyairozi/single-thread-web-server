package com.serversocket;

import java.io.IOException;
import java.net.ServerSocket;

public class Main {
    static final int SOCKET_PORT = 80;

    public static void launchServer() {
        try {
            ServerSocket serverSocket = new ServerSocket(SOCKET_PORT);
            System.out.println("Socket server started at http:://127.0.0.1:" + SOCKET_PORT);
    
            while (true) {
                ClientServer client = new ClientServer(serverSocket.accept());
                client.serve();
            }

            // System.out.println("Close the server socket");
            // serverSocket.close();
    
        } catch (IOException e) {
            System.err.println("Error starting socket server: " + e.getMessage());
        }
    }

    /**
     * Run server socket
     *
     * @param args
     */
    public static void main(String[] args) {
        launchServer();
    }

    
}
