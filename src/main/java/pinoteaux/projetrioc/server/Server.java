package pinoteaux.projetrioc.server;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;


public class Server {
    private static final int[] PORTS = {9999, 1111, 2222, 3333, 4444, 5555};
    private static final List<Integer> randomIntegers = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        for (int i = 0; i < 100; i++) {
            randomIntegers.add(new Random().nextInt(1, 5));
        }
        for (int i = 0; i < PORTS.length; i++) {
            int serverNumber = i + 1;
            ServerSocket serverSocket = new ServerSocket(PORTS[i]);
            if(i == 0){
                new Thread(() -> handleClient(serverSocket, serverNumber,200)).start();
            } else {
                new Thread(() -> handleClient(serverSocket, serverNumber, 5)).start();
            }
        }
    }

    private static void handleClient(ServerSocket serverSocket, int serverNumber, int maxUsers) {
        AtomicInteger currentUsers = new AtomicInteger();
        CopyOnWriteArrayList<Socket> connectedClients = new CopyOnWriteArrayList<>();

        try {
            System.out.println("Server " + serverNumber + " is running on port " + serverSocket.getLocalPort());

            while (true) {
                if (currentUsers.get() < maxUsers) {
                    Socket socket = serverSocket.accept();
                    connectedClients.add(socket);
                    currentUsers.getAndIncrement();
                    if(serverSocket.getLocalPort() != 9999){
                        new Thread(new ClientHandler(socket, serverNumber, new ArrayList<>(randomIntegers), connectedClients)).start();
                        if (currentUsers.get() == maxUsers) {
                            for (Socket clientSocket : connectedClients) {
                                PrintWriter pw = new PrintWriter(clientSocket.getOutputStream(), true);
                                pw.println("START");
                                pw.flush();
                                pw.println(randomIntegers.get(0));
                                pw.flush();
                            }
                            System.out.println("Game started on server " + serverNumber);
                            break;
                        }
                    } else{
                        new Thread(new ClientHandler(socket, serverNumber, connectedClients)).start();
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error in server " + serverNumber + ": " + e.getMessage());
        }
    }
}