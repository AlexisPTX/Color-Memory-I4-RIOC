package pinoteaux.projetrioc.server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import pinoteaux.projetrioc.Constantes;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * La classe Server gère le serveur multithread qui accepte les connexions des clients,
 * distribue les tâches et initialise les jeux en envoyant des messages via des sockets.
 * Elle gère plusieurs serveurs avec des configurations et un nombre d'utilisateurs limités.
 */
public class Server {

    /**
     * Les ports utilisés pour les serveurs. Le premier est global, et les suivants sont spécifiques aux serveurs de jeu.
     */
    private static final int[] PORTS = {Constantes.SERVER_GLOBAL_PORT, Constantes.SERVER_1_PORT,
            Constantes.SERVER_2_PORT, Constantes.SERVER_3_PORT, Constantes.SERVER_4_PORT, Constantes.SERVER_5_PORT};

    /**
     * Liste contenant les entiers aléatoires correspondant aux couleurs utilisés dans le jeu.
     */
    private static final List<Integer> randomIntegers = new ArrayList<>();

    /**
     * Méthode principale du serveur. Initialise les entiers aléatoires et démarre les serveurs sur les différents ports.
     *
     * @param args Arguments de la ligne de commande (non utilisés ici).
     * @throws IOException Si une erreur survient lors de la création des serveurs ou de l'acceptation des connexions.
     */
    public static void main(String[] args) throws IOException {
        // Initialisation des entiers aléatoires pour le jeu
        for (int i = 0; i < 100; i++) {
            randomIntegers.add(new Random().nextInt(1, 5));
        }

        // Création des serveurs sur les ports définis dans PORTS
        for (int i = 0; i < PORTS.length; i++) {
            int serverNumber = i + 1;
            ServerSocket serverSocket = new ServerSocket(PORTS[i]);

            // Démarrage des serveurs dans des threads séparés
            if (i == 0) {
                new Thread(() -> handleClient(serverSocket, serverNumber, 200)).start();
            } else {
                new Thread(() -> handleClient(serverSocket, serverNumber, 3)).start();
            }
        }
    }

    /**
     * Gère les connexions des clients pour chaque serveur. Accepte les connexions et initialise un jeu lorsque le
     * nombre maximum d'utilisateurs est atteint.
     *
     * @param serverSocket Le socket du serveur pour accepter les connexions des clients.
     * @param serverNumber Le numéro du serveur en cours.
     * @param maxUsers Le nombre maximum d'utilisateurs autorisés par serveur avant de démarrer un jeu.
     */
    private static void handleClient(ServerSocket serverSocket, int serverNumber, int maxUsers) {
        // Compteur d'utilisateurs connectés
        AtomicInteger currentUsers = new AtomicInteger();
        // Liste des clients connectés
        CopyOnWriteArrayList<Socket> connectedClients = new CopyOnWriteArrayList<>();

        try {
            System.out.println("Server " + serverNumber + " is running on port " + serverSocket.getLocalPort());

            while (true) {
                // Si le nombre d'utilisateurs connectés est inférieur à maxUsers, accepter une nouvelle connexion
                if (currentUsers.get() < maxUsers) {
                    Socket socket = serverSocket.accept();
                    connectedClients.add(socket);
                    currentUsers.getAndIncrement();

                    // Si ce n'est pas le serveur global, créer un ClientHandler pour gérer la communication avec le client
                    if (serverSocket.getLocalPort() != Constantes.SERVER_GLOBAL_PORT) {
                        new Thread(new ClientHandler(socket, serverNumber, new ArrayList<>(randomIntegers), connectedClients)).start();

                        // Démarrer le jeu si le nombre d'utilisateurs est atteint
                        if (currentUsers.get() == maxUsers) {
                            for (Socket clientSocket : connectedClients) {
                                PrintWriter pw = new PrintWriter(clientSocket.getOutputStream(), true);
                                Gson gson = new Gson();
                                JsonObject json = new JsonObject();
                                json.addProperty("type", "SERVER");
                                json.addProperty("message", "START");
                                json.addProperty("intDebut", randomIntegers.get(0));
                                pw.println(gson.toJson(json));
                                pw.flush();
                            }
                            System.out.println("Game started on server " + serverNumber);
                            break;
                        }
                    } else {
                        // Pour le serveur global, on n'envoie pas de séquence de jeu, on gère simplement la connexion
                        new Thread(new ClientHandler(socket, serverNumber, connectedClients)).start();
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error in server " + serverNumber + ": " + e.getMessage());
        }
    }
}
