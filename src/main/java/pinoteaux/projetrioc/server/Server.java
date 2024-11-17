package pinoteaux.projetrioc.server;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;


public class Server {
    private static final int MAX_USERS = 2;
    private static final int[] PORTS = {1111, 2222, 3333, 4444, 5555};
    private static final List<Integer> randomIntegers = new CopyOnWriteArrayList<>();

    public static void main(String[] args) throws IOException {
        for (int i = 0; i < PORTS.length; i++) {
            int serverNumber = i + 1;
            ServerSocket serverSocket = new ServerSocket(PORTS[i]);
            new Thread(() -> handleClient(serverSocket, serverNumber)).start();
        }
        for (int i = 0; i < 100; i++) {
            randomIntegers.add(new Random().nextInt(1, 5));
        }
    }

    private static void handleClient(ServerSocket serverSocket, int serverNumber) {
        AtomicInteger currentUsers = new AtomicInteger();
        List<Socket> connectedClients = new ArrayList<>();
        try {
            System.out.println("Server " + serverNumber + " is running on port " + serverSocket.getLocalPort());

            while (true) {
                if (currentUsers.get() < MAX_USERS) {
                    Socket socket = serverSocket.accept();
                    connectedClients.add(socket);
                    System.out.println("Client connected on server " + serverNumber);
                    currentUsers.getAndIncrement();

                    new Thread(new ClientHandler(socket, serverNumber, randomIntegers)).start();

                    if (currentUsers.get() == MAX_USERS) {
                        for (Socket clientSocket : connectedClients) {
                            PrintWriter pw = new PrintWriter(clientSocket.getOutputStream(), true);
                            pw.println("START");
                            pw.flush();
                            pw.println(randomIntegers.get(0));
                            pw.flush();
                        }
                        System.out.println("Game started on server " + serverNumber);
                        System.out.println("Server test first integer: " + randomIntegers.get(0));
                        break;
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error in server " + serverNumber + ": " + e.getMessage());
        }
    }
}