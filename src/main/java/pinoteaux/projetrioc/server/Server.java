package pinoteaux.projetrioc.server;

import java.io.*;
import java.net.*;
import java.util.concurrent.atomic.AtomicInteger;


public class Server {
    private static final int maxUsers = 10;

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket1 = new ServerSocket(1111);
        ServerSocket serverSocket2 = new ServerSocket(2222);
        ServerSocket serverSocket3 = new ServerSocket(3333);
        ServerSocket serverSocket4 = new ServerSocket(4444);
        ServerSocket serverSocket5 = new ServerSocket(5555);

        // Crée des threads pour gérer chaque serveur de manière indépendante
        new Thread(() -> handleClient(serverSocket1, 1)).start();
        new Thread(() -> handleClient(serverSocket2, 2)).start();
        new Thread(() -> handleClient(serverSocket3, 3)).start();
        new Thread(() -> handleClient(serverSocket4, 4)).start();
        new Thread(() -> handleClient(serverSocket5, 5)).start();
    }

    private static void handleClient(ServerSocket serverSocket, int serverNumber) {
        AtomicInteger currentUsers = new AtomicInteger();
        try {
            System.out.println("Server " + serverNumber + " is running on port " + serverSocket.getLocalPort());

            while (true) {
                if (currentUsers.get() < maxUsers) {
                    Socket socket = serverSocket.accept();
                    System.out.println("Client connected on server " + serverNumber);
                    currentUsers.getAndIncrement();
                    new Thread(() -> {
                        try {
                            handleClientConnection(socket, serverNumber);
                        } finally {
                            currentUsers.getAndDecrement();
                            System.out.println("Client disconnected from server " + serverNumber);
                        }
                    }).start();
                }
            }
        } catch (IOException e) {
            System.out.println("Error in server " + serverNumber + ": " + e.getMessage());
        }
    }


    private static void handleClientConnection(Socket socket, int serverNumber) {
        try (BufferedReader bf = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            String message;
            while ((message = bf.readLine()) != null) {
                System.out.println("Server " + serverNumber + " received: " + message);
            }
        } catch (IOException e) {
            System.out.println("Client disconnected from server " + serverNumber);
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                System.out.println("Error closing socket on server " + serverNumber + ": " + e.getMessage());
            }
        }
    }
}
