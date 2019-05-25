package network.server;

import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Scanner;

import static network.ServerClient.NON_UNIQUE_USERNAME;
import static network.ServerClient.UNIQUE_USERNAME;

public class Acceptor extends Thread {
    private static HashMap<String, ClientHandler> clientHandlers = new HashMap<>();

    private ServerSocket serverSocket;

    public Acceptor(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public static ClientHandler getClientHandler(String username) {
        return clientHandlers.get(username);
    }

    public static void removeClientHandler(ClientHandler clientHandler) {
        clientHandlers.remove(clientHandler.getUsername(), clientHandler);
    }

    @Override
    public void run() {
        Scanner scanner;
        PrintStream out;
        try {
            while (!interrupted()) {
                Socket clientSocket = serverSocket.accept();
                scanner = new Scanner(clientSocket.getInputStream());
                out = new PrintStream(clientSocket.getOutputStream(), true);
                new clientInitiator(scanner, out, clientSocket).start();
            }
        } catch (Exception ignored) {
        }
    }

    private static class clientInitiator extends Thread {
        Scanner scanner;
        PrintStream out;
        Socket clientSocket;

        public clientInitiator(Scanner scanner, PrintStream out, Socket clientSocket) {
            this.scanner = scanner;
            this.out = out;
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            initiateClient().start();
        }

        private ClientHandler initiateClient() {
            String username = getUsername(scanner, out);
            ClientHandler clientHandler = new ClientHandler(clientSocket, username);
            clientHandlers.put(username, clientHandler);
            return clientHandler;
        }

        private String getUsername(Scanner scanner, PrintStream out) {
            String username = scanner.nextLine();
            while (clientHandlers.containsKey(username) || username.isBlank()) {
                out.println(NON_UNIQUE_USERNAME);
                username = scanner.nextLine();
            }
            out.println(UNIQUE_USERNAME);
            return username;
        }
    }
}